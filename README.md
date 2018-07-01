# Summary
-calculate​ ​ realtime​ ​ statistic​ ​ from​ ​ the​ ​ last​ ​ 60​ ​ seconds

#APIs
- POST http://localhost:8083/v1/transaction-summary/transaction (to add  transaction)
    accept json as body for e.g. 
    { 
​ ​ ​ ​ ​ ​ "amount":​ ​ 12.3,
​ ​ ​    "timestamp":​ ​ 1478192204000
    }
- GET http://localhost:8083/v1/transaction-summary/summary (to get summary of the last minute transaction)
