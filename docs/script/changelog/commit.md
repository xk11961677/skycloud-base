## changelog git 提交时message格式

### 三个部分：Header，Body 和 Footer
```
<type>(<scope>): <subject>
// 空一行
<body>
// 空一行
<footer>
```

### Header
type（必需）、scope（可选）和subject（必需）
#### type用于说明 commit 的类别，只允许使用下面7个标识。
1. feat：新功能（feature）
2. fix：修补bug
3. docs：文档（documentation）
4. style： 格式（不影响代码运行的变动）
5. refactor：重构（即不是新增功能，也不是修改bug的代码变动）
6. test：增加测试
7. chore：构建过程或辅助工具的变动

#### scope用于说明 commit 影响的范围
比如数据层、控制层、视图层等等，视项目不同而不同

#### subject
subject是 commit 目的的简短描述，不超过50个字符

### Body
Body 部分是对本次 commit 的详细描述，可以分成多行

- 修改版本号1.0.1-SNAPSHOT
- 去掉xx_member库

### Footer
Footer 部分只用于两种情况
1. 不兼容变动 , 以 BREAKING CHANGE 开头
2. 关闭 Issue , 如: Closes #123, #245, #992

### Revert
还有一种特殊情况，如果当前 commit 用于撤销以前的 commit，则必须以revert:开头，后面跟着被撤销 Commit 的 Header。
```
revert: feat(pencil): add 'graphiteWidth' option

This reverts commit 667ecc1654a317a13331b17617d973392f415f02.
```

### 如格式
```
feat(all): 增加changelog

- 增加changelog

BREAKING CHANGE: 增加changelog
```