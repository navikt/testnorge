import reactCompiler from 'eslint-plugin-react-compiler'
import reactPlugin from 'eslint-plugin-react'
import tsPlugin from '@typescript-eslint/eslint-plugin'

export default [
	{
		settings: {
			react: {
				version: 'detect',
			},
			'import/resolver': {
				node: {
					paths: ['src'],
					extensions: ['.js', '.jsx', '.ts', '.tsx'],
				},
			},
		},
		plugins: {
			react: reactPlugin,
			'@typescript-eslint': tsPlugin,
			'react-compiler': reactCompiler,
		},
		extends: [
			'eslint:recommended',
			'plugin:react/recommended',
			'plugin:react/jsx-runtime',
			'plugin:@typescript-eslint/recommended',
			'eslint-config-prettier',
		],
		rules: {
			'prefer-const': 'off',
			'prefer-rest-params': 'off',
			'react/prop-types': 'off',
			'react/display-name': 'off',
			'no-shadow': 'off',
			'react/no-unescaped-entities': 'off',
			'react/jsx-no-target-blank': 'off',
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
		},
	},
]
