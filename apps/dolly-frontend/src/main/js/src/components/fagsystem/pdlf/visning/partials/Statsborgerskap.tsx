import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import * as _ from 'lodash-es'
import { initialStatsborgerskap } from '@/components/fagsystem/pdlf/form/initialValues'
import { PersonData, StatsborgerskapData } from '@/components/fagsystem/pdlf/PdlTypes'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '@/config/kodeverk'
import Formatters from '@/utils/DataFormatter'

type StatsborgerskapTypes = {
	data: Array<StatsborgerskapData>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erPdlVisning?: boolean
}

type StatsborgerskapLesTypes = {
	statsborgerskapData: StatsborgerskapData
	idx: number
}

type StatsborgerskapVisningTypes = {
	statsborgerskapData: StatsborgerskapData
	idx: number
	data: Array<StatsborgerskapData>
	tmpPersoner: Array<PersonData>
	ident: string
	erPdlVisning: boolean
}

const StatsborgerskapLes = ({ statsborgerskapData, idx }: StatsborgerskapLesTypes) => {
	if (statsborgerskapData) {
		return (
			<div className="person-visning_redigerbar" key={idx}>
				<TitleValue
					title="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					value={statsborgerskapData.landkode}
				/>
				<TitleValue
					title="Statsborgerskap registrert"
					value={Formatters.formatDate(statsborgerskapData.gyldigFraOgMed)}
				/>
				<TitleValue
					title="Statsborgerskap til"
					value={Formatters.formatDate(statsborgerskapData.gyldigTilOgMed)}
				/>
			</div>
		)
	}
	return null
}

const StatsborgerskapVisning = ({
	statsborgerskapData,
	idx,
	data,
	tmpPersoner,
	ident,
	erPdlVisning,
}: StatsborgerskapVisningTypes) => {
	const initStatsborgerskap = Object.assign(_.cloneDeep(initialStatsborgerskap), data[idx])
	const initialValues = { statsborgerskap: initStatsborgerskap }

	const redigertStatsborgerskapPdlf = _.get(tmpPersoner, `${ident}.person.statsborgerskap`)?.find(
		(a: StatsborgerskapData) => a.id === statsborgerskapData.id
	)
	const slettetStatsborgerskapPdlf =
		tmpPersoner?.hasOwnProperty(ident) && !redigertStatsborgerskapPdlf
	if (slettetStatsborgerskapPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const statsborgerskapValues = redigertStatsborgerskapPdlf
		? redigertStatsborgerskapPdlf
		: statsborgerskapData
	const redigertStatsborgerskapValues = redigertStatsborgerskapPdlf
		? {
				statsborgerskap: Object.assign(
					_.cloneDeep(initialStatsborgerskap),
					redigertStatsborgerskapPdlf
				),
		  }
		: null
	return erPdlVisning ? (
		<StatsborgerskapLes statsborgerskapData={statsborgerskapData} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={<StatsborgerskapLes statsborgerskapData={statsborgerskapValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertStatsborgerskapValues}
			path="statsborgerskap"
			ident={ident}
		/>
	)
}

export const Statsborgerskap = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning,
}: StatsborgerskapTypes) => {
	if (data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Statsborgerskap" nested>
					{(borgerskap: StatsborgerskapData, idx: number) => (
						<StatsborgerskapVisning
							statsborgerskapData={borgerskap}
							idx={idx}
							data={data}
							tmpPersoner={tmpPersoner}
							ident={ident}
							erPdlVisning={erPdlVisning}
						/>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
