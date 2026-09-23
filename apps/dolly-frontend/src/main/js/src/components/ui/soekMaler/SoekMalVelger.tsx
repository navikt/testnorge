import { Box, HelpText, HStack, InlineMessage, UNSAFE_Combobox as Combobox } from '@navikt/ds-react'
import React, { useState } from 'react'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { useSoekMalerBruker, useSoekMalerOversikt } from '@/utils/hooks/useTemplateSearch'
import { getBrukerOptions } from '@/components/bestillingsveileder/startModal/MalVelgerIdent'
import { Mal } from '@/utils/hooks/useMaler'

interface SoekMalOption {
	value: string
	label: string
	soekKriterier: Record<string, unknown>
}

export function getSoekMalOptions(maler: Mal[] | undefined): SoekMalOption[] {
	if (!Array.isArray(maler) || maler.length < 1) return []
	return maler.map((mal) => ({
		value: String(mal.id),
		label: mal.malNavn,
		soekKriterier: mal.soekKriterier,
	}))
}

interface SoekMalVelgerProps {
	velgMal: (soekKriterier: Record<string, unknown>) => void
}

export const SoekMalVelger = ({ velgMal }: SoekMalVelgerProps) => {
	const { currentBruker } = useCurrentBruker()
	const [valgtBruker, setValgtBruker] = useState(
		currentBruker?.representererTeam?.brukerId ?? currentBruker?.brukerId,
	)

	const { brukere, loading: loadingBrukere, error: errorBrukere } = useSoekMalerOversikt()

	const { maler, loading: loadingMaler, error: errorMaler } = useSoekMalerBruker(valgtBruker)

	const malerInfotekst = `Om du ofte gjør samme søk kan det være lurt å lage maler av disse søkene. For å lage en mal fyller du først ut søkeskjemaet med ønskede verdier, for så å trykke på knappen "Opprett mal fra søk" som ligger under søkeskjemaet.`

	const malerLabel = (
		<HStack gap="space-8">
			<label>Velg mal for søk</label>
			<HelpText title="Informasjon om maler for søk">
				{!loadingMaler && (!maler || maler?.length < 1)
					? `Du har foreløpig ingen maler for søk. ${malerInfotekst}`
					: `Her kan du velge en mal for søket ditt. ${malerInfotekst}`}
			</HelpText>
		</HStack>
	)

	const malOptions = getSoekMalOptions(maler)
	const brukerOptions = getBrukerOptions(brukere) ?? []
	const valgtBrukerOption = brukerOptions?.filter((option) => option.value === valgtBruker) ?? []

	return (
		<Box
			background="accent-moderate"
			borderColor="accent"
			borderWidth="1"
			padding="space-12"
			borderRadius="4"
			style={{ marginBottom: '12px' }}
		>
			<HStack gap="space-16" wrap={false} width="100%">
				<Box flexGrow="2" flexBasis="0">
					<Combobox
						label="Bruker/team"
						options={brukerOptions}
						selectedOptions={valgtBrukerOption}
						onToggleSelected={(bruker) => {
							setValgtBruker(bruker ?? '')
						}}
						isLoading={loadingBrukere}
					/>
				</Box>
				<Box flexGrow="3" flexBasis="0">
					<Combobox
						label={malerLabel}
						options={malOptions}
						isLoading={loadingMaler}
						onToggleSelected={(mal, isSelected) => {
							const soekKriterier = isSelected
								? malOptions?.find((m) => m.value === mal)?.soekKriterier
								: {}
							velgMal(soekKriterier ?? {})
						}}
					/>
				</Box>
			</HStack>
			{errorBrukere && !loadingBrukere && (
				<InlineMessage
					status="error"
					style={{ marginTop: '10px' }}
				>{`Feil ved henting av brukere: ${errorBrukere}`}</InlineMessage>
			)}
			{errorMaler && !loadingMaler && !loadingBrukere && (
				<InlineMessage
					status="error"
					style={{ marginTop: '10px' }}
				>{`Feil ved henting av maler: ${errorMaler}`}</InlineMessage>
			)}
		</Box>
	)
}
