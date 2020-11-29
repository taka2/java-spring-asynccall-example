# Java/Springで非同期メソッドを実装する際の比較

## メソッド呼び出しと戻り値取得の評価

| # | やり方 | 実装の手間 | 戻り値の扱い | 例外の扱い |
----|----|----|----|----
|1| 古いやり方 | 面倒なので非推奨 | 面倒なので非推奨 | 面倒なので非推奨 | 
|2| ExecutorServiceを使ったやり方 | スレッドプールの細かい設定が必要 | Futureに内包されるものを取り出す | ExecutionExceptionに内包されるものを取り出す |
|3| CompletableFutureを使ったやり方 | スレッドプールの細かい設定が不要（設定することもできる） | CompletableFutureに内包されるものを取り出す | ExecutionExceptionに内包されるものを取り出す |
|4| @Asyncを使ったやり方（Spring） | 簡単（スレッドプールの設定ができない？） | 戻り値として表現できる（自然） | throws節として表現できる（自然） |

## 結論
とにかく非同期でメソッド呼び出ししたくて、かつ、Springを使っているのであれば、#4の@Asyncを使えばよい。

Springを使っていない場合は、#2のやり方がよいと思われる。

#1は論外として、#3は一見よさそうだが、検査例外が投げられないので、使える場面とそうでない場面が分かれてしまう。
#3を全体で統一して使うにはつらいのではないか思われる。

## 実行結果（参考）
### AsyncRunner.java
```
3
3
3
3
3
```

### ExceptionHandlerRunner.java
```
java.lang.Exception: old
java.util.concurrent.ExecutionException: java.lang.Exception: new1
java.util.concurrent.ExecutionException: java.lang.RuntimeException: new2
java.lang.Exception: new3
```
