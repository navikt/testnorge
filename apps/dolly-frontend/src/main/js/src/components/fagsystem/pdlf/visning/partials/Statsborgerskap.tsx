import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import * as _ from 'lodash-es'
import { getInitialStatsborgerskap } from '@/components/fagsystem/pdlf/form/initialValues'
import { PersonData, StatsborgerskapData } from '@/components/fagsystem/pdlf/PdlTypes'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '@/config/kodeverk'
import { formatDate } from '@/utils/DataFormatter'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type StatsborgerskapTypes = {
	data: Array<StatsborgerskapData>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erPdlVisning?: boolean
	identtype?: string
	erRedigerbar?: boolean
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
	identtype?: string
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
					value={formatDate(statsborgerskapData.gyldigFraOgMed)}
				/>
				<TitleValue
					title="Statsborgerskap til"
					value={formatDate(statsborgerskapData.gyldigTilOgMed)}
				/>
				<TitleValue title="Master" value={statsborgerskapData.master} />
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
	identtype,
}: StatsborgerskapVisningTypes) => {
	const initStatsborgerskap = Object.assign(_.cloneDeep(getInitialStatsborgerskap()), data[idx])
	const initialValues = { statsborgerskap: initStatsborgerskap }

	const redigertStatsborgerskapPdlf = _.get(tmpPersoner, `${ident}.person.statsborgerskap`)?.find(
		(a: StatsborgerskapData) => a.id === statsborgerskapData.id,
	)
	const slettetStatsborgerskapPdlf =
		tmpPersoner?.hasOwnProperty(ident) && !redigertStatsborgerskapPdlf
	if (slettetStatsborgerskapPdlf) {
		return <OpplysningSlettet />
	}

	const statsborgerskapValues = redigertStatsborgerskapPdlf
		? redigertStatsborgerskapPdlf
		: statsborgerskapData
	const redigertStatsborgerskapValues = redigertStatsborgerskapPdlf
		? {
				statsborgerskap: Object.assign(
					_.cloneDeep(getInitialStatsborgerskap()),
					redigertStatsborgerskapPdlf,
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
			identtype={identtype}
		/>
	)
}

export const Statsborgerskap = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning,
	identtype,
	erRedigerbar = true,
}: StatsborgerskapTypes) => {
	if (data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Statsborgerskap" nested>
					{(borgerskap: StatsborgerskapData, idx: number) =>
						erRedigerbar ? (
							<StatsborgerskapVisning
								statsborgerskapData={borgerskap}
								idx={idx}
								data={data}
								tmpPersoner={tmpPersoner}
								ident={ident}
								erPdlVisning={erPdlVisning}
								identtype={identtype}
							/>
						) : (
							<StatsborgerskapLes statsborgerskapData={borgerskap} idx={idx} />
						)
					}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
