import { render, screen } from '@testing-library/react'
import { vi } from 'vitest'
import type { ReactNode } from 'react'
import BrukeradferdPage from '@/pages/adminPages/Brukeradferd/BrukeradferdPage'
import { useErDollyAdmin } from '@/utils/DollyAdmin'
import { useBrukeradferdData } from '@/pages/adminPages/Brukeradferd/useBrukeradferdData'

vi.mock('@/utils/DollyAdmin', () => ({
	useErDollyAdmin: vi.fn(),
}))

vi.mock('@/pages/adminPages/AdminAccessDenied', () => ({
	AdminAccessDenied: () => <p>Du har ikke tilgang til denne siden!</p>,
}))

vi.mock('@/pages/adminPages/Brukeradferd/useBrukeradferdData', () => ({
	FAGSYSTEM_DETALJER_ID: 'brukeradferd-fagsystem-detaljer',
	useBrukeradferdData: vi.fn(),
}))

vi.mock('@/pages/adminPages/Brukeradferd/brukeradferdSections', () => ({
	BrukeradferdPanel: ({ children }: { children: ReactNode }) => <section>{children}</section>,
	FagsystemFordelingSection: () => <div>Fordeling</div>,
	NyePersonerSection: () => <div>Nye personer</div>,
	AdferdOverTidSection: () => <div>Over tid</div>,
	ValgtPunktSection: () => <div>Valgt punkt</div>,
	FagsystemDetaljerSection: () => <div>Detaljer</div>,
}))

describe('BrukeradferdPage', () => {
	it('should deny access for non-admin users', () => {
		vi.mocked(useErDollyAdmin).mockReturnValue(false)

		render(<BrukeradferdPage />)

		expect(screen.getByText('Du har ikke tilgang til denne siden!')).toBeTruthy()
		expect(useBrukeradferdData).not.toHaveBeenCalled()
	})

	it('should render dashboard sections and errors for admin users', () => {
		vi.mocked(useErDollyAdmin).mockReturnValue(true)
		vi.mocked(useBrukeradferdData).mockReturnValue({
			mockModeEnabled: false,
			brukeradferdError: new Error('Tidsavbrudd'),
			valgtPunkt: null,
		} as unknown as ReturnType<typeof useBrukeradferdData>)

		render(<BrukeradferdPage />)

		expect(screen.getByRole('heading', { level: 1, name: 'Brukeradferd' })).toBeTruthy()
		expect(screen.getByText('Klarte ikke å hente brukeradferd: Tidsavbrudd')).toBeTruthy()
		expect(screen.getByText(/Statistikk over bestillinger fra alle Dolly-brukere/)).toBeTruthy()
		const seksjoner = ['Nye personer', 'Fordeling', 'Over tid', 'Detaljer'].map((tekst) =>
			screen.getByText(tekst),
		)
		seksjoner.slice(1).forEach((seksjon, index) => {
			expect(
				seksjoner[index].compareDocumentPosition(seksjon) & Node.DOCUMENT_POSITION_FOLLOWING,
			).toBeTruthy()
		})
		expect(screen.queryByText('Valgt punkt')).toBeNull()
	})

	it('should render selected point section when a point is selected', () => {
		vi.mocked(useErDollyAdmin).mockReturnValue(true)
		vi.mocked(useBrukeradferdData).mockReturnValue({
			mockModeEnabled: false,
			loadingAdferd: false,
			valgtPunkt: '2026-09-02',
		} as unknown as ReturnType<typeof useBrukeradferdData>)

		render(<BrukeradferdPage />)

		expect(screen.getByText('Valgt punkt')).toBeTruthy()
	})
})
