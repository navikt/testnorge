import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Loading from '@/components/ui/loading/Loading'
import { Alert } from '@navikt/ds-react'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import {
	codeToNorskLabel,
	formatDateTimeWithSeconds,
	oversettBoolean,
	showLabel,
} from '@/utils/DataFormatter'
import { useFengsel } from '@/utils/hooks/useInstitusjon'
import { publiseringstidspunktTid } from '@/components/fagsystem/kdi/form/Form'
import { getTidspunktLabel } from '@/components/fagsystem/kdi/utils'

// TODO: Fiks denne
export const sjekkManglerKdiData = (kdiData) => {
	return false
	// return kdiData?.length < 1 || kdiData?.every((miljoData) => miljoData.data?.length < 1)
}

export const KdiMelding = ({ melding, id, annulleringListe }) => {
	const annullering = annulleringListe?.find((a) => a.annullertMeldingId === melding.meldingId)

	const { fengsler, loading, error } = useFengsel()
	const fengselLabel = fengsler
		? `${melding.organisasjonsnummer} - ${fengsler[melding.organisasjonsnummer]}`
		: melding.organisasjonsnummer

	const tidspunktLabel = getTidspunktLabel(melding.type)

	return (
		<>
			{melding.type && <h4 style={{ marginTop: '0px' }}>{codeToNorskLabel(melding.type)}</h4>}
			<div className="person-visning_content" key={id} style={{ marginBottom: 0 }}>
				<TitleValue
					title="Publiseringstidspunkt"
					value={formatDateTimeWithSeconds(melding.publiseringstidspunkt)}
				/>
				<TitleValue title="Kategori" value={showLabel('kdiKategori', melding.kategori)} />
				<TitleValue
					title="Organisasjonsnummer"
					value={melding.organisasjonsnummer ? fengselLabel : null}
				/>
				<TitleValue title={tidspunktLabel} value={formatDateTimeWithSeconds(melding.tidspunkt)} />
				<TitleValue
					title="Er overført til utenlandsk fengsel"
					value={oversettBoolean(melding.erOverfoertTilUtlandskfengsel)}
				/>
				<TitleValue
					title="Er overført til varetekt med elektronisk kontroll"
					value={oversettBoolean(melding.erOverfoertTilVaretektMedElektroniskKontroll)}
				/>
				<TitleValue
					title="Forventet tidspunkt for slutt på straffeavbrudd"
					value={formatDateTimeWithSeconds(melding.forventetAvbruddSluttTidspunkt)}
				/>
			</div>
			{annullering && (
				<>
					<h4 style={{ marginTop: '0px' }}>Annullering</h4>
					<div className="person-visning_content">
						<TitleValue
							title="Publiseringstidspunkt"
							value={formatDateTimeWithSeconds(annullering.publiseringstidspunkt)}
						/>
					</div>
				</>
			)}
		</>
	)
}

export const KdiVisning = ({ data, loading, harKdiBestilling }) => {
	if (!data && !harKdiBestilling) {
		return null
	}

	if (loading) {
		return <Loading label="Laster KDI-meldinger ..." />
	}

	const annullering = data.annullering

	const meldinger = Object.entries(data)
		.flatMap(([type, values]) => values?.map((melding) => ({ ...melding, type })))
		?.filter((melding) => melding && melding.type !== 'annullering')
		?.sort(
			(a, b) =>
				publiseringstidspunktTid(a.publiseringstidspunkt) -
				publiseringstidspunktTid(b.publiseringstidspunkt),
		)

	const manglerFagsystemdata = harKdiBestilling && (!data || !meldinger || meldinger?.length < 1)

	return (
		<div>
			<SubOverskrift
				label="KDI-meldinger"
				iconKind="institusjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ingen KDI-meldinger på person
				</Alert>
			) : (
				<ErrorBoundary>
					<DollyFieldArray data={meldinger} nested>
						{(melding, idx) => (
							<KdiMelding melding={melding} id={idx} annulleringListe={annullering} />
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</div>
	)
}
