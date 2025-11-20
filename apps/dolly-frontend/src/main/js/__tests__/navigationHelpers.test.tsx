import { describe, expect, it, vi } from 'vitest'
import {
	executeMutateAndValidate,
	validateAndNavigate,
} from '@/components/bestillingsveileder/stegVelger/utils/navigationHelpers'
import { ShowErrorContextType } from '@/components/bestillingsveileder/ShowErrorContext'

const createMockFormMethods = (overrides = {}) => {
	return {
		trigger: vi.fn(async () => true),
		formState: {
			errors: {},
		},
		clearErrors: vi.fn(),
		...overrides,
	} as any
}

const createMockErrorContext = (): ShowErrorContextType => ({
	showError: false,
	setShowError: vi.fn(),
})

describe('validateAndNavigate', () => {
	it('should validate form and navigate to next step on success', async () => {
		const formMethods = createMockFormMethods()
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId', 'antall', 'pdldata.opprettNyPerson.identtype']
		const setStep = vi.fn()

		const result = await validateAndNavigate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			1,
			setStep,
		)

		expect(formMethods.trigger).toHaveBeenCalledWith(validationPaths)
		expect(setStep).toHaveBeenCalledWith(2)
		expect(result).toBe(true)
	})

	it('should show error and not navigate when gruppeId validation fails', async () => {
		const formMethods = createMockFormMethods({
			formState: {
				errors: { gruppeId: { message: 'Required' } },
			},
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId', 'antall']
		const setStep = vi.fn()

		const result = await validateAndNavigate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			0,
			setStep,
		)

		expect(errorContext.setShowError).toHaveBeenCalledWith(true)
		expect(setStep).not.toHaveBeenCalled()
		expect(result).toBe(false)
	})

	it('should show error and not navigate when antall validation fails', async () => {
		const formMethods = createMockFormMethods({
			formState: {
				errors: { antall: { message: 'Invalid number' } },
			},
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId', 'antall']
		const setStep = vi.fn()

		const result = await validateAndNavigate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			0,
			setStep,
		)

		expect(errorContext.setShowError).toHaveBeenCalledWith(true)
		expect(setStep).not.toHaveBeenCalled()
		expect(result).toBe(false)
	})

	it('should show error and not navigate when identtype validation fails', async () => {
		const formMethods = createMockFormMethods({
			formState: {
				errors: { pdldata: { opprettNyPerson: { identtype: { message: 'Required' } } } },
			},
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['pdldata.opprettNyPerson.identtype']
		const setStep = vi.fn()

		const result = await validateAndNavigate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			0,
			setStep,
		)

		expect(errorContext.setShowError).toHaveBeenCalledWith(true)
		expect(setStep).not.toHaveBeenCalled()
		expect(result).toBe(false)
	})

	it('should navigate even with only environment errors', async () => {
		const formMethods = createMockFormMethods({
			formState: {
				errors: { environments: { message: 'Invalid environment' } },
			},
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId', 'environments']
		const setStep = vi.fn()

		const result = await validateAndNavigate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			1,
			setStep,
		)

		expect(setStep).toHaveBeenCalledWith(2)
		expect(result).toBe(true)
	})

	it('should not navigate when there are other errors besides environments', async () => {
		const formMethods = createMockFormMethods({
			formState: {
				errors: {
					environments: { message: 'Invalid environment' },
					pdldata: { person: { navn: { message: 'Required' } } },
				},
			},
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId', 'pdldata', 'environments']
		const setStep = vi.fn()

		const result = await validateAndNavigate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			2,
			setStep,
		)

		expect(errorContext.setShowError).toHaveBeenCalledWith(true)
		expect(setStep).not.toHaveBeenCalled()
		expect(result).toBe(false)
	})
})

describe('executeMutateAndValidate', () => {
	it('should execute mutation, clear errors, and navigate on success', async () => {
		const formMethods = createMockFormMethods({
			clearErrors: vi.fn(),
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId', 'antall']
		const setStep = vi.fn()
		const formMutate = vi.fn(async () => ({ status: 'SUCCESS' }))
		const setMutateLoading = vi.fn()
		const manualMutateFields = ['manual.sykemelding.detaljertSykemelding']

		await executeMutateAndValidate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			1,
			setStep,
			formMutate,
			setMutateLoading,
			manualMutateFields,
		)

		expect(formMethods.clearErrors).toHaveBeenCalledWith(manualMutateFields)
		expect(errorContext.setShowError).toHaveBeenCalledWith(true)
		expect(setMutateLoading).toHaveBeenCalledWith(true)
		expect(formMutate).toHaveBeenCalled()
		expect(setMutateLoading).toHaveBeenCalledWith(false)
		expect(setStep).toHaveBeenCalledWith(2)
	})

	it('should not navigate when mutation returns INVALID status', async () => {
		const formMethods = createMockFormMethods({
			clearErrors: vi.fn(),
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId']
		const setStep = vi.fn()
		const formMutate = vi.fn(async () => ({ status: 'INVALID' }))
		const setMutateLoading = vi.fn()
		const manualMutateFields = ['manual.sykemelding.detaljertSykemelding']

		await executeMutateAndValidate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			1,
			setStep,
			formMutate,
			setMutateLoading,
			manualMutateFields,
		)

		expect(formMutate).toHaveBeenCalled()
		expect(setMutateLoading).toHaveBeenCalledWith(false)
		expect(setStep).not.toHaveBeenCalled()
	})

	it('should handle mutation error and still attempt validation', async () => {
		const formMethods = createMockFormMethods({
			clearErrors: vi.fn(),
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId', 'antall']
		const setStep = vi.fn()
		const formMutate = vi.fn(async () => {
			throw new Error('Mutation failed')
		})
		const setMutateLoading = vi.fn()
		const manualMutateFields = ['manual.sykemelding.detaljertSykemelding']

		await executeMutateAndValidate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			1,
			setStep,
			formMutate,
			setMutateLoading,
			manualMutateFields,
		)

		expect(setMutateLoading).toHaveBeenCalledWith(false)
		expect(setStep).toHaveBeenCalledWith(2)
	})

	it('should set loading states correctly during mutation lifecycle', async () => {
		const formMethods = createMockFormMethods({
			clearErrors: vi.fn(),
		})
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId']
		const setStep = vi.fn()
		let mutationInProgress = false
		const formMutate = vi.fn(async () => {
			mutationInProgress = true
			await new Promise((resolve) => setTimeout(resolve, 10))
			mutationInProgress = false
			return { status: 'SUCCESS' }
		})
		const setMutateLoading = vi.fn((loading) => {
			if (loading && !mutationInProgress) {
			}
		})
		const manualMutateFields = ['manual.sykemelding.detaljertSykemelding']

		await executeMutateAndValidate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			0,
			setStep,
			formMutate,
			setMutateLoading,
			manualMutateFields,
		)

		expect(setMutateLoading).toHaveBeenCalledWith(true)
		expect(setMutateLoading).toHaveBeenCalledWith(false)
	})

	it('should clear manual mutate fields before mutation', async () => {
		const clearErrors = vi.fn()
		const formMethods = createMockFormMethods({ clearErrors })
		const errorContext = createMockErrorContext()
		const validationPaths = ['gruppeId']
		const setStep = vi.fn()
		const formMutate = vi.fn(async () => ({ status: 'SUCCESS' }))
		const setMutateLoading = vi.fn()
		const manualMutateFields = ['manual.sykemelding.detaljertSykemelding', 'manual.custom.field']

		await executeMutateAndValidate(
			{ formMethods: formMethods as any, errorContext, validationPaths },
			1,
			setStep,
			formMutate,
			setMutateLoading,
			manualMutateFields,
		)

		expect(clearErrors).toHaveBeenCalledWith(manualMutateFields)
		expect(clearErrors).toHaveBeenCalledBefore(formMutate as any)
	})
})
