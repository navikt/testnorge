module.exports = {
	preset: 'ts-jest/presets/js-with-babel',
	testEnvironment: 'node',
	setupTestFrameworkScriptFile: '<rootDir>/testSetup.js',
	moduleNameMapper: {
		'(nav-frontend-)(.*)(style)$': '<rootDir>/__mocks__/styleMock.js',
		'\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4)$': '<rootDir>/__mocks__/fileMock.js',
		'\\.(css|less)$': '<rootDir>/__mocks__/styleMock.js',
		'~/(.*)$': '<rootDir>/src/$1',
	},
	collectCoverageFrom: [
		'src/**/*.js',
		'!src/**/*Connector.js',
		'!src/**/*WithHoc.js',
		'!src/service/services/**/*.js',
		'!src/pages/**/*.js',
		'!src/*.js',
	],
}
