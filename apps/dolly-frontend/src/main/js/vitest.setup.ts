import { cleanup } from '@testing-library/react'
import { act } from 'react'
import * as matchers from '@testing-library/jest-dom/matchers'
import { test as testBase } from 'vitest'
import { worker } from './__tests__/mocks/browser.ts'
;(globalThis as any).IS_REACT_ACT_ENVIRONMENT = true

const isBrowser = typeof window !== 'undefined'

const flushMicrotasks = () => new Promise((resolve) => queueMicrotask(resolve))
let workerStartPromise: ReturnType<typeof worker.start> | undefined

const startWorker = () => {
	workerStartPromise ??= worker.start({ quiet: true })
	return workerStartPromise
}

export const dollyTest = testBase.extend({
	worker: [
		async (_context, use) => {
			if (isBrowser) {
				await startWorker()
			}
			await use(worker)
			if (isBrowser) {
				worker.resetHandlers()
			}
		},
		{ auto: true },
	],
})

expect.extend(matchers)

beforeEach(() => {
	const originalError = console.error
	console.error = (...args: any[]) => {
		const message = args[0]?.toString() || ''
		if (
			message.includes('A suspended resource finished loading inside a test') ||
			message.includes('act(...)')
		) {
			return
		}
		originalError.apply(console, args)
	}
})

afterEach(async () => {
	cleanup()
	await act(async () => {
		await flushMicrotasks()
	})
})
