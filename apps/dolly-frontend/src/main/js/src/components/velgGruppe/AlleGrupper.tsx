import { useAlleBrukere } from '@/utils/hooks/useBruker'
import { Gruppe, useEgneGrupper } from '@/utils/hooks/useGruppe'
import React, { useState } from 'react'
import { useFormContext } from 'react-hook-form'
import { Box, HStack, UNSAFE_Combobox as Combobox } from '@navikt/ds-react'

interface AlleGrupper {
	fraGruppe?: number | null
}

export default ({ fraGruppe = null }: AlleGrupper) => {
	const formMethods = useFormContext()
	const { brukere, loading: loadingBrukere } = useAlleBrukere()
	const [valgtBruker, setValgtBruker] = useState(formMethods?.watch('bruker') || '')

	const brukerOptions = brukere?.map((bruker) => {
		const erTeam = bruker?.brukertype === 'TEAM'
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

	const valgtGruppeId = formMethods.watch('gruppeId')
	const selectedBruker = brukerOptions?.filter((option) => option.value === valgtBruker) ?? []
	const selectedGruppe = gruppeOptions?.filter((option) => option.value === valgtGruppeId) ?? []

	return (
		<HStack gap="space-16" width="100%" wrap={false}>
			<Box flexGrow="1" flexBasis="0">
				<Combobox
					name={'bruker'}
					label={'Bruker/team'}
					options={brukerOptions ?? []}
					selectedOptions={selectedBruker}
					onToggleSelected={(bruker) => {
						formMethods?.setValue('bruker', bruker)
						formMethods?.trigger('bruker')
						setValgtBruker(bruker || '')
					}}
					isLoading={loadingBrukere}
					placeholder={loadingBrukere ? 'Laster brukere ...' : 'Velg bruker ...'}
				/>
			</Box>
			<Box flexGrow="1" flexBasis="0">
				<Combobox
					name="gruppeId"
					label="Gruppe"
					options={gruppeOptions ?? []}
					selectedOptions={selectedGruppe}
					onToggleSelected={(gruppe) => {
						formMethods?.setValue('gruppeId', gruppe)
						formMethods.trigger('gruppeId')
					}}
					isLoading={loadingGrupper}
					placeholder={loadingGrupper ? 'Laster grupper ...' : 'Velg gruppe ...'}
					disabled={!valgtBruker}
				/>
			</Box>
		</HStack>
	)
}
