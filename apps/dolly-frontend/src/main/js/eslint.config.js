import js from '@eslint/js'
import eslintReact from '@eslint-react/eslint-plugin'
import prettier from 'eslint-config-prettier'
import reactCompiler from 'eslint-plugin-react-compiler'
import tsPlugin from '@typescript-eslint/eslint-plugin'

export default [
	{
		ignores: [
			'.vitest*/**',
			'build/**',
			'coverage/**',
			'node_modules/**',
			'playwright/.cache/**',
			'playwright-report/**',
			'test-results/**',
		],
	},
	js.configs.recommended,
	...tsPlugin.configs['flat/recommended'],
	eslintReact.configs['recommended-typescript'],
	{
		files: ['**/*.{js,jsx,ts,tsx}'],
		settings: {
			'import/resolver': {
				node: {
					paths: ['src'],
					extensions: ['.js', '.jsx', '.ts', '.tsx'],
				},
			},
		},
		plugins: {
			'react-compiler': reactCompiler,
		},
		rules: {
			'prefer-const': 'off',
			'prefer-rest-params': 'off',
			'no-shadow': 'off',
			'@typescript-eslint/camelcase': 'off',
			'@typescript-eslint/ban-ts-comment': 'off',
			'@typescript-eslint/no-namespace': 'off',
			'@typescript-eslint/no-shadow': 'off',
			'@typescript-eslint/no-empty-function': 'off',
			'@typescript-eslint/no-unused-vars': 'off',
			'@typescript-eslint/no-var-requires': 'off',
			'@typescript-eslint/no-explicit-any': 'off',
			'@typescript-eslint/no-use-before-define': 'off',
			'@typescript-eslint/explicit-module-boundary-types': 'off',
			'@typescript-eslint/explicit-function-return-type': 'off',
			'react-compiler/react-compiler': 'warn',
		},
	},
	prettier,
]
