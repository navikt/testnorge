import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '@/config/kodeverk'
import { formatDate } from '@/utils/DataFormatter'
import { Statsborgerskap } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import React from 'react'
import { StatsborgerskapVisning } from '@/components/fagsystem/pdlf/visning/partials/Statsborgerskap'

type StatsborgerskapProps = {
	data: Statsborgerskap
	idx?: number
}

type VisningProps = {
	statsborgerskapListe: [Statsborgerskap]
}

const StatsborgerskapPdlVisning = ({ data, idx }: StatsborgerskapProps) => {
	if (data) {
		return (
			<div key={idx} className="person-visning_content">
				<TitleValue
					title="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					value={data.land}
				/>
				<TitleValue title="Statsborgerskap registrert" value={formatDate(data.gyldigFraOgMed)} />
				<TitleValue title="Statsborgerskap til" value={formatDate(data.gyldigTilOgMed)} />
			</div>
		)
	}
	return null
}

const StatsborgerskapVisningRedigerbar = ({
	data,
	idx,
	alleData,
	tmpPersoner,
	ident,
	identtype,
	master,
}) => {
	return (
		<div className="person-visning_content">
			<StatsborgerskapVisning
				statsborgerskapData={data}
				idx={idx}
				data={alleData}
				tmpPersoner={tmpPersoner}
				ident={ident}
				erPdlVisning={false}
				identtype={identtype}
				master={master}
			/>
		</div>
	)
}

export const PdlStatsborgerskap = ({
	statsborgerskapListe,
	pdlfData,
	tmpPersoner,
	ident,
	identtype,
}: VisningProps) => {
	if (statsborgerskapListe?.length < 1 && (!tmpPersoner || Object.keys(tmpPersoner).length < 1)) {
		return null
	}

	const gyldigeStatsborgerskap = statsborgerskapListe.filter(
		(borgerskap: Statsborgerskap) => !borgerskap.metadata?.historisk,
	)
	const historiskeStatsborgerskap = statsborgerskapListe.filter(
		(borgerskap: Statsborgerskap) => borgerskap.metadata?.historisk,
	)

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ArrayHistorikk
				component={StatsborgerskapPdlVisning}
				componentRedigerbar={StatsborgerskapVisningRedigerbar}
				data={gyldigeStatsborgerskap}
				pdlfData={pdlfData}
				historiskData={historiskeStatsborgerskap}
				tmpPersoner={tmpPersoner}
				ident={ident}
				identtype={identtype}
				header="Statsborgerskap"
			/>
		</div>
	)
}
