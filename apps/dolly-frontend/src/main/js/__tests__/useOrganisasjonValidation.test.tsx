import { beforeEach, describe, expect, it, Mock, vi } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { useForm } from 'react-hook-form'
import { useOrganisasjonValidation } from '@/components/shared/ArbeidsforholdToggle/useOrganisasjonValidation'
import { useOrganisasjonForvalter } from '@/utils/hooks/useDollyOrganisasjoner'
import { handleManualOrgChange } from '@/utils/OrgUtils'

vi.mock('@/utils/hooks/useDollyOrganisasjoner', () => ({
	useOrganisasjonForvalter: vi.fn(),
}))

vi.mock('@/utils/OrgUtils', () => ({
	handleManualOrgChange: vi.fn(),
}))

const mockUseOrganisasjonForvalter = useOrganisasjonForvalter as Mock
const mockHandleManualOrgChange = handleManualOrgChange as Mock

const ORG_PATH = 'arbeidsgiver.orgnummer'
const ERROR_PATH = `manual.${ORG_PATH}`

const mockForvalterReturn = (
	overrides: Partial<ReturnType<typeof useOrganisasjonForvalter>> = {},
) => {
	mockUseOrganisasjonForvalter.mockReturnValue({
		organisasjoner: [],
		loading: false,
		error: undefined,
		hasBeenCalled: false,
		...overrides,
	})
}

const renderValidationHook = (watchedOrgnr: string, useValidation = true) => {
	return renderHook(
		({ orgnr, validate }) => {
			const formMethods = useForm()
			useOrganisasjonValidation({
				formMethods,
				organisasjonPath: ORG_PATH,
				watchedOrgnr: orgnr,
				useValidation: validate,
			})
			return formMethods
		},
		{
			initialProps: { orgnr: watchedOrgnr, validate: useValidation },
		},
	)
}

const getErrorMessage = (formMethods: ReturnType<typeof useForm>) => {
	return formMethods.getFieldState(ERROR_PATH)?.error?.message
}

describe('useOrganisasjonValidation', () => {
	beforeEach(() => {
		vi.clearAllMocks()
	})

	it('shouldShowNineSifferErrorForShortInput', async () => {
		mockForvalterReturn({ hasBeenCalled: false })

		const { result } = renderValidationHook('12345')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBe('Organisasjonsnummer må være 9 siffer')
		})
	})

	it('shouldShowNineSifferErrorForLongInput', async () => {
		mockForvalterReturn({ hasBeenCalled: false })

		const { result } = renderValidationHook('1234567890')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBe('Organisasjonsnummer må være 9 siffer')
		})
	})

	it('shouldShowNotFoundErrorWhenApiReturnsEmpty', async () => {
		mockForvalterReturn({
			organisasjoner: [],
			loading: false,
			hasBeenCalled: true,
		})

		const { result } = renderValidationHook('123456789')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBe('Fant ikke organisasjonen')
		})
	})

	it('shouldClearErrorWhenOrganisasjonFound', async () => {
		const mockOrg = {
			q1: { organisasjonsnummer: '123456789', enhetstype: 'BEDR', juridiskEnhet: '987654321' },
		}
		mockForvalterReturn({
			organisasjoner: [mockOrg],
			loading: false,
			hasBeenCalled: true,
		})

		const { result } = renderValidationHook('123456789')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBeUndefined()
		})
	})

	it('shouldCallHandleManualOrgChangeWhenOrgFound', async () => {
		const mockOrg = {
			q1: { organisasjonsnummer: '123456789', enhetstype: 'BEDR', juridiskEnhet: '987654321' },
		}
		mockForvalterReturn({
			organisasjoner: [mockOrg],
			loading: false,
			hasBeenCalled: true,
		})

		renderValidationHook('123456789')

		await waitFor(() => {
			expect(mockHandleManualOrgChange).toHaveBeenCalledWith(
				'123456789',
				expect.any(Object),
				ORG_PATH,
				null,
				mockOrg.q1,
			)
		})
	})

	it('shouldNotSetErrorWhileLoading', async () => {
		mockForvalterReturn({
			organisasjoner: [],
			loading: true,
			hasBeenCalled: false,
		})

		const { result } = renderValidationHook('123456789')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBeUndefined()
		})
	})

	it('shouldClearErrorWhenInputIsCleared', async () => {
		mockForvalterReturn({ hasBeenCalled: false })
		const { result, rerender } = renderValidationHook('12345')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBe('Organisasjonsnummer må være 9 siffer')
		})

		rerender({ orgnr: '', validate: true })

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBeUndefined()
		})
	})

	it('shouldNotValidateWhenUseValidationIsFalse', async () => {
		mockForvalterReturn({ hasBeenCalled: false })

		const { result } = renderValidationHook('123', false)

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBeUndefined()
		})
	})

	it('shouldPassCorrectOrgnummereToForvalterHook', () => {
		mockForvalterReturn()

		renderValidationHook('123456789', true)

		expect(mockUseOrganisasjonForvalter).toHaveBeenCalledWith(['123456789'])
	})

	it('shouldPassEmptyArrayToForvalterWhenValidationDisabled', () => {
		mockForvalterReturn()

		renderValidationHook('123456789', false)

		expect(mockUseOrganisasjonForvalter).toHaveBeenCalledWith([])
	})

	it('shouldTransitionFromNineSifferErrorToNotFoundError', async () => {
		mockForvalterReturn({ hasBeenCalled: false })
		const { result, rerender } = renderValidationHook('12345')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBe('Organisasjonsnummer må være 9 siffer')
		})

		mockForvalterReturn({
			organisasjoner: [],
			loading: false,
			hasBeenCalled: true,
		})

		rerender({ orgnr: '123456789', validate: true })

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBe('Fant ikke organisasjonen')
		})
	})

	it('shouldTransitionFromNotFoundToFoundWhenOrgAppears', async () => {
		mockForvalterReturn({
			organisasjoner: [],
			loading: false,
			hasBeenCalled: true,
		})

		const { result, rerender } = renderValidationHook('123456789')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBe('Fant ikke organisasjonen')
		})

		const mockOrg = {
			q1: { organisasjonsnummer: '987654321', enhetstype: 'BEDR', juridiskEnhet: '111111111' },
		}
		mockForvalterReturn({
			organisasjoner: [mockOrg],
			loading: false,
			hasBeenCalled: true,
		})

		rerender({ orgnr: '987654321', validate: true })

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBeUndefined()
		})
	})

	it('shouldUseQ2WhenQ1IsNotAvailable', async () => {
		const mockOrg = {
			q2: { organisasjonsnummer: '123456789', enhetstype: 'AAFY', juridiskEnhet: '987654321' },
		}
		mockForvalterReturn({
			organisasjoner: [mockOrg],
			loading: false,
			hasBeenCalled: true,
		})

		const { result } = renderValidationHook('123456789')

		await waitFor(() => {
			expect(getErrorMessage(result.current)).toBeUndefined()
		})

		await waitFor(() => {
			expect(mockHandleManualOrgChange).toHaveBeenCalledWith(
				'123456789',
				expect.any(Object),
				ORG_PATH,
				null,
				mockOrg.q2,
			)
		})
	})
})
