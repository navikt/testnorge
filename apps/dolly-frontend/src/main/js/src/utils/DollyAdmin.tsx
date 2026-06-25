import { useCurrentBruker } from '@/utils/hooks/useBruker'

type CurrentBrukerType = {
	currentBruker?: {
		grupper?: Array<string>
	}
}

export const useErDollyAdmin = () => {
	const { currentBruker }: CurrentBrukerType = useCurrentBruker()
	const grupper = currentBruker?.grupper
	const adminGruppe = '728ae02e-04e8-48b9-ab65-ee6233b5003f'
	return grupper?.includes(adminGruppe)
}
