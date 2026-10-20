import { render, renderHook, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { FieldValues, FormProvider, useForm } from 'react-hook-form'
import InntektStub from '@/components/fagsystem/inntektstub/validerInntekt'
import InntektstubService from '@/service/services/inntektstub/InntektstubService'

vi.mock('@/service/services/inntektstub/InntektstubService', () => ({
	default: { validate: vi.fn() },
}))

vi.mock('@/components/ui/form/inputs/select/Select', () => ({
	FormSelect: () => null,
}))

vi.mock('@/components/ui/form/inputs/textInput/TextInput', () => ({
	FormTextInput: () => null,
}))

vi.mock('@/components/ui/form/inputs/datepicker/Datepicker', () => ({
	FormDatepicker: () => null,
}))

vi.mock(
	'@/components/fagsystem/inntektstub/form/partials/inntektsinformasjonLister/inntektForm',
	() => ({ initialValues: {} }),
)

const inntektPath = 'inntektstub.inntektsinformasjon.0.inntektsliste.0'

const pensjonGyldigeVerdier = {
	beskrivelse: ['alderspensjon'],
	tilleggsinformasjonstype: ['<TOM>', 'AldersUfoereEtterlatteAvtalefestetOgKrigspensjon'],
	grunnpensjonsbeloep: ['<UTFYLT>'],
	persontype: ['<TOM>'],
}

const mockValidate = vi.mocked(InntektstubService.validate)

const setup = (inntekt: Record<string, unknown>) => {
	const { result } = renderHook(() =>
		useForm<FieldValues>({
			defaultValues: { inntektstub: { inntektsinformasjon: [{ inntektsliste: [inntekt] }] } },
		}),
	)
	const formMethods = result.current
	const view = (
		<FormProvider {...formMethods}>
			<InntektStub inntektPath={inntektPath} />
		</FormProvider>
	)
	return { formMethods, view }
}

describe('InntektStub tilleggsinformasjon', () => {
	beforeEach(() => {
		mockValidate.mockReset()
		mockValidate.mockResolvedValue(pensjonGyldigeVerdier)
	})

	it('should flatten nested tilleggsinformasjon from a template on mount', async () => {
		const { formMethods, view } = setup({
			beloep: 1000,
			inntektstype: 'PENSJON_ELLER_TRYGD',
			beskrivelse: 'alderspensjon',
			tilleggsinformasjon: { pensjon: { grunnpensjonsbeloep: 1234 } },
		})

		render(view)

		await waitFor(() => {
			expect(formMethods.getValues(inntektPath)).toMatchObject({
				tilleggsinformasjonstype: 'AldersUfoereEtterlatteAvtalefestetOgKrigspensjon',
				grunnpensjonsbeloep: 1234,
			})
		})
		expect(formMethods.getValues(`${inntektPath}.tilleggsinformasjon`)).toBeUndefined()
	})

	it('should send tilleggsinformasjon values to the validate API', async () => {
		const { view } = setup({
			inntektstype: 'PENSJON_ELLER_TRYGD',
			beskrivelse: 'alderspensjon',
			tilleggsinformasjonstype: 'AldersUfoereEtterlatteAvtalefestetOgKrigspensjon',
			grunnpensjonsbeloep: '1234',
		})

		render(view)

		await waitFor(() => {
			expect(mockValidate).toHaveBeenCalledWith(
				expect.objectContaining({
					tilleggsinformasjonstype: 'AldersUfoereEtterlatteAvtalefestetOgKrigspensjon',
					grunnpensjonsbeloep: '1234',
				}),
			)
		})
	})

	it('should keep tilleggsinformasjon values when returning to the step', async () => {
		const inntekt = {
			inntektstype: 'PENSJON_ELLER_TRYGD',
			beskrivelse: 'alderspensjon',
			tilleggsinformasjonstype: 'AldersUfoereEtterlatteAvtalefestetOgKrigspensjon',
			grunnpensjonsbeloep: '1234',
		}
		const { formMethods, view } = setup(inntekt)
		const { unmount } = render(view)
		await waitFor(() => expect(mockValidate).toHaveBeenCalled())
		unmount()

		render(view)

		await waitFor(() => expect(mockValidate).toHaveBeenCalledTimes(2))
		await waitFor(() => {
			expect(formMethods.getValues(inntektPath)).toMatchObject(inntekt)
		})
	})

	it('should clear a field the API marks as empty-only', async () => {
		const { formMethods, view } = setup({
			inntektstype: 'PENSJON_ELLER_TRYGD',
			beskrivelse: 'alderspensjon',
			persontype: 'kunde',
		})

		render(view)

		await waitFor(() => {
			expect(formMethods.getValues(`${inntektPath}.persontype`)).toBeUndefined()
		})
	})
})
