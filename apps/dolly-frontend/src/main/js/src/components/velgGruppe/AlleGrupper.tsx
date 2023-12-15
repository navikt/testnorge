import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import Loading from '@/components/ui/loading/Loading'
import { useAlleBrukere, useCurrentBruker } from '@/utils/hooks/useBruker'
import { Gruppe, useEgneGrupper, useGrupper } from '@/utils/hooks/useGruppe'
import React, { useState } from 'react'

interface AlleGrupper {
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
	valgtGruppe: string
	fraGruppe?: number | null
}

type Options = {
	value: string
	label: string
}

export default ({ setValgtGruppe, valgtGruppe, fraGruppe = null }: AlleGrupper) => {
	const { currentBruker } = useCurrentBruker()
	const { brukere, loading: loadingBrukere } = useAlleBrukere()
	const [valgtBruker, setValgtBruker] = useState(null)

	const filteredBrukerliste = brukere?.filter(
		(bruker) => bruker.brukerId !== currentBruker.brukerId,
	)

	const brukerOptions = filteredBrukerliste?.map((bruker) => {
		return {
			value: bruker?.brukerId,
			label: bruker?.brukernavn,
		}
	})

	const { grupper, loading: loadingGrupper } = useEgneGrupper(valgtBruker)

	const filteredGruppeliste = grupper?.contents?.filter((gruppe) => gruppe.id !== fraGruppe)

	const gruppeOptions = filteredGruppeliste?.map((gruppe: Gruppe) => {
		return {
			value: gruppe.id,
			label: `${gruppe.id} - ${gruppe.navn}`,
		}
	})

	return (
		<div className="flexbox--flex-wrap">
			<DollySelect
				name={'Bruker'}
				label={'Bruker'}
				options={brukerOptions}
				size="medium"
				onChange={(bruker) => setValgtBruker(bruker?.value || null)}
				value={valgtBruker}
				isLoading={loadingBrukere}
				placeholder={loadingBrukere ? 'Laster brukere ...' : 'Velg bruker ...'}
			/>
			<DollySelect
				name="Gruppe"
				label="Gruppe"
				options={gruppeOptions}
				onChange={(gruppe: Options) => setValgtGruppe(gruppe?.value)}
				value={valgtGruppe}
				size={fraGruppe ? 'grow' : 'large'}
				isClearable={false}
				isDisabled={!valgtBruker}
				isLoading={loadingGrupper}
				placeholder={loadingGrupper ? 'Laster grupper ...' : 'Velg gruppe ...'}
			/>
		</div>
	)
}
