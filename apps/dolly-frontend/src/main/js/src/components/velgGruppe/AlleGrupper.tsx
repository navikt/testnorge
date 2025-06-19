import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { useAlleBrukere } from '@/utils/hooks/useBruker'
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
	const { brukere, loading: loadingBrukere } = useAlleBrukere()
	const [valgtBruker, setValgtBruker] = useState(formMethods?.watch('bruker') || '')

	const brukerOptions = brukere?.map((bruker) => {
		const erTeam = bruker.brukerId.includes('team')
		return {
			value: bruker?.brukerId,
			label: bruker?.brukernavn + (erTeam ? ' (team)' : ''),
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
				label={'Bruker/team'}
				options={brukerOptions}
				size="medium"
				onChange={(bruker) => {
					formMethods?.setValue('bruker', bruker?.value)
					formMethods?.trigger('bruker')
					setValgtBruker(bruker?.value || '')
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
					formMethods.trigger('gruppeId')
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
