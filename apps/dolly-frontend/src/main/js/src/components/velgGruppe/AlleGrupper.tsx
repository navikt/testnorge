import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { useAlleBrukere, useCurrentBruker } from '@/utils/hooks/useBruker'
import { Gruppe, useEgneGrupper } from '@/utils/hooks/useGruppe'
import React, { useState } from 'react'
import { useFormContext } from 'react-hook-form'

interface AlleGrupper {
	fraGruppe?: number | null
}

type Options = {
	value: string
	label: string
}

export default ({ fraGruppe = null }: AlleGrupper) => {
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()
	const { brukere, loading: loadingBrukere } = useAlleBrukere()
	const [valgtBruker, setValgtBruker] = useState(formMethods?.watch('bruker') || null)

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
				name={'bruker'}
				label={'Bruker'}
				options={brukerOptions}
				size="medium"
				onChange={(bruker) => {
					formMethods.setValue('bruker', bruker?.value)
					setValgtBruker(bruker?.value || null)
				}}
				value={valgtBruker || formMethods?.watch('bruker')}
				isLoading={loadingBrukere}
				placeholder={loadingBrukere ? 'Laster brukere ...' : 'Velg bruker ...'}
			/>
			<DollySelect
				name="gruppeId"
				label="Gruppe"
				options={gruppeOptions}
				value={formMethods?.watch('gruppeId')}
				onChange={(gruppe: Options) => {
					formMethods?.setValue('gruppeId', gruppe?.value)
				}}
				size={fraGruppe ? 'grow' : 'large'}
				isClearable={false}
				isDisabled={!valgtBruker}
				isLoading={loadingGrupper}
				placeholder={loadingGrupper ? 'Laster grupper ...' : 'Velg gruppe ...'}
			/>
		</div>
	)
}
