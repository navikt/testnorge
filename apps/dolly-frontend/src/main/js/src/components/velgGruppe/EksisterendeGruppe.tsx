import React from 'react'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import Loading from '~/components/ui/loading/Loading'
import { useCurrentBruker } from '~/utils/hooks/useBruker'
import { Gruppe, useGrupper } from '~/utils/hooks/useGruppe'
import { useSelector } from 'react-redux'

interface EksisterendeGruppe {
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
	valgtGruppe: string
	fraGruppe?: number
}

type Options = {
	value: string
	label: string
}

export default ({ setValgtGruppe, valgtGruppe, fraGruppe = null }: EksisterendeGruppe) => {
	const { sidetall, sideStoerrelse } = useSelector((state: any) => state.finnPerson)
	const {
		currentBruker: { brukerId },
	} = useCurrentBruker()
	const { grupper, loading } = useGrupper(sidetall, sideStoerrelse, brukerId)

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
			name="Gruppe"
			label="Gruppe"
			options={gruppeOptions}
			onChange={(gruppe: Options) => setValgtGruppe(gruppe.value)}
			value={valgtGruppe}
			size={fraGruppe ? 'grow' : 'large'}
			isClearable={false}
		/>
	)
}
