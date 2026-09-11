// ============================================================
// npm init -y
// npm install mariadb
// node init-tables.js
// ============================================================

const mariadb = require('mariadb');

// ============================================================
// DB 서버 설정
// ============================================================

const DB_HOST = 'localhost';
const DB_PORT = 3307;


// ============================================================
// 실행 SQL
// ============================================================

const CREATE_ORDER_TABLE_SQL = `
CREATE TABLE product_order (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_number VARCHAR(255),
    product_name VARCHAR(255),
    shipping_address VARCHAR(255),
    shipping_status VARCHAR(255),
    member_name VARCHAR(255),
    PRIMARY KEY (id)
)
`;

const INSERT_SAMPLE_ORDERS_SQL = `
INSERT INTO product_order (
    order_number,
    product_name,
    shipping_address,
    shipping_status,
    member_name
) VALUES (
    'H001',
    '맥북에어',
    '서울시 강남구 역삼동',
    '상품준비중',
    'seojun'
), (
    'H002',
    '아이폰',
    '서울시 영등포구 여의도동',
    '배송중',
    'seojun'
)
`;


// ============================================================
// DB 계정 목록
// ============================================================

const accounts = [
    {
        database: 'tutor',
        user: 'tutor',
        password: 'tutorp'
    },

    ...Array.from({ length: 30 }, (_, i) => {
        const number = i + 1;

        return {
            database: `edu${number}`,
            user: `edu${number}`,
            password: `edu${number}p`
        };
    })
];


// ============================================================
// 식별자(table name) escape
// ============================================================

function escapeIdentifier(name) {
    return `\`${name.replace(/`/g, '``')}\``;
}


// ============================================================
// 개별 DB 초기화
// ============================================================

async function initializeDatabase(account) {
    let connection;
    let droppedTableCount = 0;

    try {
        console.log(`[${account.database}] 작업 시작`);

        connection = await mariadb.createConnection({
            host: DB_HOST,
            port: DB_PORT,
            user: account.user,
            password: account.password,
            database: account.database
        });

        // 현재 데이터베이스에 존재하는 모든 일반 테이블 조회
        const tables = await connection.query(`
            SELECT TABLE_NAME
            FROM information_schema.TABLES
            WHERE TABLE_SCHEMA = ?
              AND TABLE_TYPE = 'BASE TABLE'
        `, [account.database]);

        // 외래 키 관계가 있는 테이블도 순서와 관계없이 삭제할 수 있도록 처리
        await connection.query('SET FOREIGN_KEY_CHECKS = 0');

        try {
            for (const table of tables) {
                const tableName = table.TABLE_NAME;

                await connection.query(
                    `DROP TABLE IF EXISTS ${escapeIdentifier(tableName)}`
                );

                droppedTableCount += 1;
                console.log(`[${account.database}] DROP TABLE ${tableName}`);
            }
        } finally {
            await connection.query('SET FOREIGN_KEY_CHECKS = 1');
        }

        // 주문 목록 테이블 생성 및 초기 샘플 주문 목록 추가
        await connection.query(CREATE_ORDER_TABLE_SQL);
        await connection.query(INSERT_SAMPLE_ORDERS_SQL);

        console.log(
            `[${account.database}] 성공 (기존 ${droppedTableCount}개 테이블 삭제, 주문 샘플 추가)`
        );

        return {
            database: account.database,
            user: account.user,
            droppedTables: droppedTableCount,
            result: 'SUCCESS',
            message: ''
        };

    } catch (error) {
        console.error(`[${account.database}] 실패: ${error.message}`);

        return {
            database: account.database,
            user: account.user,
            droppedTables: droppedTableCount,
            result: 'FAIL',
            message: error.message
        };

    } finally {
        if (connection) {
            await connection.end();
        }
    }
}


// ============================================================
// 전체 DB 처리
// ============================================================

async function main() {
    console.log();
    console.log('==============================================');
    console.log(' MariaDB 전체 테이블 삭제 및 주문 테이블 초기화 시작');
    console.log('==============================================');
    console.log(`Server : ${DB_HOST}:${DB_PORT}`);
    console.log(`대상 DB : ${accounts.length}개`);
    console.log('==============================================');
    console.log();

    const results = [];

    for (const account of accounts) {
        const result = await initializeDatabase(account);
        results.push(result);
    }

    console.log();
    console.log('==============================================');
    console.log('초기화 결과');
    console.log('==============================================');

    console.table(results);

    const successCount = results.filter(result => result.result === 'SUCCESS').length;
    const failCount = results.filter(result => result.result === 'FAIL').length;
    const droppedTableCount = results.reduce(
        (sum, result) => sum + result.droppedTables,
        0
    );

    console.log(`전체 DB : ${results.length}`);
    console.log(`성공    : ${successCount}`);
    console.log(`실패    : ${failCount}`);
    console.log(`삭제된 테이블 : ${droppedTableCount}`);

    if (failCount > 0) {
        console.log();
        console.log('실패한 DB');
        console.table(results.filter(result => result.result === 'FAIL'));
    }

    console.log();
    console.log('==============================================');
    console.log(' MariaDB 전체 테이블 삭제 및 주문 테이블 초기화 완료');
    console.log('==============================================');
}


// ============================================================
// 실행
// ============================================================

main().catch(error => {
    console.error('프로그램 실행 중 오류가 발생했습니다.');
    console.error(error);
    process.exit(1);
});
