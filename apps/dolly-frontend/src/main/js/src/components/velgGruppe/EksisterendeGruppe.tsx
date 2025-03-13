import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import Loading from '@/components/ui/loading/Loading'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { Gruppe, useEgneGrupper } from '@/utils/hooks/useGruppe'
import React from 'react'

interface EksisterendeGruppe {
	fraGruppe?: number
}

export default ({ fraGruppe }: EksisterendeGruppe) => {
	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()
	const { grupper, loading } = useEgneGrupper(brukerId)

	const filteredGruppeliste = grupper?.contents?.filter((gruppe) => gruppe.id !== fraGruppe)

	const gruppeOptions = filteredGruppeliste?.map((gruppe: Gruppe) => {
		return {
			value: gruppe.id,
			label: `${gruppe.id} - ${gruppe.navn}`,
		}
	})

	if (loading) {
		return <Loading label="Laster grupper" />
	}

	return (
		<DollySelect
			name="gruppeId"
			label="Gruppe"
			options={gruppeOptions}
			size={fraGruppe ? 'grow' : 'large'}
			isClearable={false}
		/>
	)
}
