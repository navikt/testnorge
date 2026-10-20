import { Gruppe } from '@/utils/hooks/useGruppe'
import React from 'react'
import { UNSAFE_Combobox as Combobox } from '@navikt/ds-react'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface EksisterendeGruppe {
	formMethods: UseFormReturn
	fraGruppe?: number
	grupper?: any
	loading?: boolean
}

export default ({ formMethods, fraGruppe, grupper, loading }: EksisterendeGruppe) => {
	const filteredGruppeliste = grupper?.contents?.filter((gruppe: any) => gruppe.id !== fraGruppe)

	const gruppeOptions = filteredGruppeliste?.map((gruppe: Gruppe) => {
		return {
			value: gruppe.id,
			label: `${gruppe.id} - ${gruppe.navn}`,
		}
	})

	const valgtGruppeId = formMethods.watch('gruppeId')
	const selectedOptions =
		gruppeOptions?.filter((option: any) => option.value === valgtGruppeId) ?? []

	return (
		<Combobox
			name="gruppeId"
			label="Gruppe"
			options={gruppeOptions ?? []}
			selectedOptions={selectedOptions}
			onToggleSelected={(option, isSelected) => {
				formMethods.setValue('gruppeId', isSelected ? option : null)
				formMethods.trigger()
			}}
			isLoading={loading}
			placeholder={loading ? 'Laster grupper ...' : 'Velg gruppe ...'}
		/>
	)
}
