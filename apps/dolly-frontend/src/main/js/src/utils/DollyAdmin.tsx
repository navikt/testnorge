import { useCurrentBruker } from '@/utils/hooks/useBruker'

type CurrentBrukerType = {
	currentBruker?: {
		grupper?: Array<string>
	}
}

export const erDollyAdmin = () => {
	const { currentBruker }: CurrentBrukerType = useCurrentBruker()
	const grupper = currentBruker?.grupper
	const teamDollyGruppe = '9c7efec1-1599-4216-a67e-6fd53a6a951c'
	return grupper?.includes(teamDollyGruppe)
}
