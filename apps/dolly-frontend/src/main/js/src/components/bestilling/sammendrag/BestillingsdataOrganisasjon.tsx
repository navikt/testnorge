import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Enhetstre, OrgTree } from '@/components/enhetstre'
import React, { useState } from 'react'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDate, showLabel } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'

const AdresseVisning = ({ adresseData }) => {
	if (!adresseData) {
		return null
	}
	const filterAdresselinjer = adresseData.adresselinjer.filter(Boolean)

	return (
		<BestillingData>
			<TitleValue
				title="Land"
				value={adresseData.landkode}
				kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
			/>
			<TitleValue title="Postnummer" value={adresseData.postnr} />
			<TitleValue title="Kommunenummer" value={adresseData.kommunenr} />
			<TitleValue title="Adresse" value={arrayToString(filterAdresselinjer, ', ')} />
		</BestillingData>
	)
}

export const BestillingsdataOrganisasjon = ({ bestilling }) => {
	const [selectedId, setSelectedId] = useState('0')
	if (!bestilling) {
		return null
	}
	const orgTree = new OrgTree(bestilling, '0')
	const selected = orgTree.find(selectedId)

	const forretningsadresse = selected?.forretningsadresse
	const postadresse = selected?.postadresse

	return (
		<ErrorBoundary>
			<div className="bestilling-visning">
				<BestillingTitle>Organisasjonsoversikt</BestillingTitle>
				<Enhetstre enheter={[orgTree]} selectedEnhet={selectedId} onNodeClick={setSelectedId} />
				<div className="bestilling-blokk">
					<BestillingData>
						<TitleValue title="Enhetstype" value={selected.enhetstype} />
						<TitleValue title="Næringskode" value={selected.naeringskode} />
						<TitleValue title="Sektorkode" value={selected.sektorkode} />
						<TitleValue title="Formål" value={selected.formaal} />
						<TitleValue title="Stiftelsesdato" value={formatDate(selected.stiftelsesdato)} />
						<TitleValue title="Målform" value={showLabel('maalform', selected.maalform)} />
						<TitleValue title="Telefon" value={selected.telefon} />
						<TitleValue title="E-postadresse" value={selected.epost} />
						<TitleValue title="Internettadresse" value={selected.nettside} />
					</BestillingData>
					{forretningsadresse && (
						<>
							<h3>Forretningsadresse</h3>
							<AdresseVisning adresseData={forretningsadresse} />
						</>
					)}
					{postadresse && (
						<>
							<h3>Postadresse</h3>
							<AdresseVisning adresseData={postadresse} />
						</>
					)}
				</div>
			</div>
		</ErrorBoundary>
	)
}
