import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom/vitest'
import { describe, expect, it, vi } from 'vitest'
import { Inntektstub } from '@/components/fagsystem/inntektstub/bestilling/Inntektstub'

vi.mock('@/components/ui/titleValue/TitleValue', () => ({
	TitleValue: ({ title, value }: { title: string; value: unknown }) =>
		value || value === 0 ? <div>{`${title}: ${value}`}</div> : null,
}))

vi.mock('@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning', () => ({
	BestillingTitle: ({ children }: { children: React.ReactNode }) => <h3>{children}</h3>,
}))

vi.mock('@/components/ui/form/fieldArray/DollyFieldArray', () => ({
	DollyFieldArray: ({
		data,
		children,
	}: {
		data: Array<unknown>
		children: (item: unknown, idx: number) => React.ReactNode
	}) => <>{data.map((item, idx) => children(item, idx))}</>,
}))

const renderInntekt = (inntekt: Record<string, unknown>) =>
	render(<Inntektstub inntektstub={{ inntektsinformasjon: [{ inntektsliste: [inntekt] }] }} />)

describe('Inntektstub bestilling', () => {
	it('should show tilleggsinformasjon from flat form values', () => {
		renderInntekt({
			beloep: 1000,
			tilleggsinformasjonstype: 'AldersUfoereEtterlatteAvtalefestetOgKrigspensjon',
			grunnpensjonsbeloep: 1234,
		})

		expect(screen.getByText('Grunnpensjonsbeløp: 1234')).toBeInTheDocument()
	})

	it('should show tilleggsinformasjon from nested values', () => {
		renderInntekt({
			beloep: 1000,
			tilleggsinformasjon: { pensjon: { grunnpensjonsbeloep: 1234 } },
		})

		expect(screen.getByText('Grunnpensjonsbeløp: 1234')).toBeInTheDocument()
	})
})
