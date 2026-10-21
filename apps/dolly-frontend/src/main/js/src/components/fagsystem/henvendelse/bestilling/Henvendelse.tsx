import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { oversettBoolean } from '@/utils/DataFormatter'
import { HenvendelseKodeverk } from '@/config/kodeverk'
import { useAlleNavEnheter } from '@/utils/hooks/useNorg2'
import { useHenvendelseTemagrupper } from '@/utils/hooks/useHenvendelse'
import {
	HenvendelseTypes,
	MeldingTypes,
	SamtalereferatTypes,
} from '@/components/fagsystem/henvendelse/HenvendelseTypes'

interface HenvendelseProps {
	henvendelse?: HenvendelseTypes
}

interface HenvendelseVerdierProps {
	data: SamtalereferatTypes
	getTemagruppeLabel: (kode?: string) => string | undefined
	getEnhetLabel: (enhetNr?: string) => string | undefined
}

const HenvendelseVerdier = ({
	data,
	getTemagruppeLabel,
	getEnhetLabel,
}: HenvendelseVerdierProps) => (
	<>
		<TitleValue title="Temagruppe" value={getTemagruppeLabel(data.temagruppe)} />
		<TitleValue title="Tema" value={data.tema} kodeverk={HenvendelseKodeverk.Tema} />
		<TitleValue title="Enhet" value={getEnhetLabel(data.enhet)} />
		<TitleValue title="Kjede-ID" value={data.kjedeId} />
		<TitleValue title="Meldings-ID" value={data.meldingsId} />
		<TitleValue title="Fritekst" value={data.fritekst} />
	</>
)

export const Henvendelse = ({ henvendelse }: HenvendelseProps) => {
	const { temagrupper } = useHenvendelseTemagrupper()
	const { alleNavEnheter } = useAlleNavEnheter()

	const meldinger = henvendelse?.meldinger ?? []
	const samtalereferater = henvendelse?.samtalereferater ?? []

	if (meldinger.length === 0 && samtalereferater.length === 0) {
		return null
	}

	const getTemagruppeLabel = (kode?: string) =>
		temagrupper.find((temagruppe) => temagruppe.value === kode)?.label ?? kode

	const getEnhetLabel = (enhetNr?: string) =>
		alleNavEnheter.find((enhet) => enhet.value === enhetNr)?.label ?? enhetNr

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Henvendelse</BestillingTitle>
				{meldinger.length > 0 && (
					<DollyFieldArray header="Melding" data={meldinger}>
						{(melding: MeldingTypes, idx: number) => (
							<React.Fragment key={idx}>
								<HenvendelseVerdier
									data={melding}
									getTemagruppeLabel={getTemagruppeLabel}
									getEnhetLabel={getEnhetLabel}
								/>
								<TitleValue title="Tildel meg" value={oversettBoolean(melding.tildelMeg)} />
							</React.Fragment>
						)}
					</DollyFieldArray>
				)}
				{samtalereferater.length > 0 && (
					<DollyFieldArray header="Samtalereferat" data={samtalereferater}>
						{(samtalereferat: SamtalereferatTypes, idx: number) => (
							<React.Fragment key={idx}>
								<HenvendelseVerdier
									data={samtalereferat}
									getTemagruppeLabel={getTemagruppeLabel}
									getEnhetLabel={getEnhetLabel}
								/>
							</React.Fragment>
						)}
					</DollyFieldArray>
				)}
			</ErrorBoundary>
		</div>
	)
}
