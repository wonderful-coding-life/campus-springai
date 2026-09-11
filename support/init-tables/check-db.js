const mariadb = require('mariadb');

// ============================================================
// DB 서버 설정
// ============================================================

const DB_HOST = 'localhost';
const DB_PORT = 3307;

const TABLES = [
    'vector_store',
    'SPRING_AI_CHAT_MEMORY'
];

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

function escapeIdentifier(name) {
    return `\`${name.replace(/`/g, '``')}\``;
}

async function checkDatabase(account) {
    let connection;

    try {
        console.log(`[${account.database}] 접속 시도`);

        connection = await mariadb.createConnection({
            host: DB_HOST,
            port: DB_PORT,
            user: account.user,
            password: account.password,
            database: account.database
        });

        const tableRows = await connection.query(`
            SELECT TABLE_NAME
            FROM information_schema.TABLES
            WHERE TABLE_SCHEMA = ?
              AND TABLE_NAME IN (?, ?)
              AND TABLE_TYPE = 'BASE TABLE'
        `, [account.database, ...TABLES]);

        const existingTables = new Set(tableRows.map(row => row.TABLE_NAME));
        const counts = {};

        for (const tableName of TABLES) {
            if (!existingTables.has(tableName)) {
                counts[tableName] = 'NOT_FOUND';
                continue;
            }

            const rows = await connection.query(
                `SELECT COUNT(*) AS rowCount FROM ${escapeIdentifier(tableName)}`
            );

            counts[tableName] = Number(rows[0].rowCount);
        }

        console.log(
            `[${account.database}] 접속 성공 - ` +
            `vector_store: ${counts.vector_store}, ` +
            `SPRING_AI_CHAT_MEMORY: ${counts.SPRING_AI_CHAT_MEMORY}`
        );

        return {
            database: account.database,
            user: account.user,
            result: 'SUCCESS',
            vector_store: counts.vector_store,
            SPRING_AI_CHAT_MEMORY: counts.SPRING_AI_CHAT_MEMORY,
            message: ''
        };
    } catch (error) {
        console.error(`[${account.database}] 접속/조회 실패: ${error.message}`);

        return {
            database: account.database,
            user: account.user,
            result: 'FAIL',
            vector_store: '-',
            SPRING_AI_CHAT_MEMORY: '-',
            message: error.message
        };
    } finally {
        if (connection) {
            await connection.end();
        }
    }
}

async function main() {
    console.log();
    console.log('==============================================');
    console.log(' MariaDB DB 계정 및 테이블 row 수 확인 시작');
    console.log('==============================================');
    console.log(`Server : ${DB_HOST}:${DB_PORT}`);
    console.log(`대상 DB: ${accounts.length}개`);
    console.log();

    const results = [];

    for (const account of accounts) {
        results.push(await checkDatabase(account));
    }

    console.log();
    console.log('==============================================');
    console.log(' DB 계정 및 테이블 row 수 확인 결과');
    console.log('==============================================');
    console.table(results);

    const successCount = results.filter(result => result.result === 'SUCCESS').length;
    const failCount = results.length - successCount;

    console.log(`전체 DB: ${results.length}`);
    console.log(`성공   : ${successCount}`);
    console.log(`실패   : ${failCount}`);

    if (failCount > 0) {
        console.log();
        console.log('실패한 DB');
        console.table(results.filter(result => result.result === 'FAIL'));
    }

    console.log();
    console.log('==============================================');
    console.log(' MariaDB DB 계정 및 테이블 row 수 확인 완료');
    console.log('==============================================');
}

main().catch(error => {
    console.error('프로그램 실행 중 오류가 발생했습니다.');
    console.error(error);
    process.exit(1);
});
