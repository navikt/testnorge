import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import { initialPersonstatus } from '@/components/fagsystem/pdlf/form/initialValues'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import Panel from '@/components/ui/panel/Panel'

const PersonstatusLes = ({ data, idx }) => {
	return (
		<div className="person-visning_redigerbar" key={idx}>
			<TitleValue title="Status" value={showLabel('personstatus', data?.status)} />
			<TitleValue title="Gyldig fra og med" value={formatDate(data?.gyldigFraOgMed)} />
			<TitleValue title="Gyldig til og med" value={formatDate(data?.gyldigTilOgMed)} />
		</div>
	)
}

const PersonstatusVisning = ({ personstatusData, idx, data, tmpPersoner, ident }) => {
	const initPersonstatus = Object.assign(_.cloneDeep(initialPersonstatus), data[idx])
	const initialValues = { folkeregisterpersonstatus: initPersonstatus }

	const redigertPersonstatusPdlf = _.get(
		tmpPersoner,
		`${ident}.person.folkeregisterPersonstatus`,
	)?.find((a) => a.id === personstatusData.id)

	const slettetPersonstatusPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertPersonstatusPdlf
	if (slettetPersonstatusPdlf) {
		return <OpplysningSlettet />
	}

	const personstatusValues = redigertPersonstatusPdlf ? redigertPersonstatusPdlf : personstatusData
	const redigertPersonstatusValues = redigertPersonstatusPdlf
		? {
				folkeregisterpersonstatus: Object.assign(
					_.cloneDeep(initialPersonstatus),
					redigertPersonstatusPdlf,
				),
			}
		: null

	return (
		<VisningRedigerbarConnector
			dataVisning={<PersonstatusLes data={personstatusValues} />}
			initialValues={initialValues}
			redigertAttributt={redigertPersonstatusValues}
			path="folkeregisterpersonstatus"
			ident={ident}
		/>
	)
}

export const Personstatus = ({ data, tmpPersoner, ident }) => {
	if ((!data || data.length === 0) && (!tmpPersoner || Object.keys(tmpPersoner).length < 1)) {
		return null
	}

	const tmpData = _.get(tmpPersoner, `${ident}.person.folkeregisterPersonstatus`)
	if ((!data || data.length === 0) && (!tmpData || tmpData.length < 1)) {
		return null
	}

	const gyldigPersonstatus = data[0]
	const historiskePersonstatuser = data.slice(1)

	return (
		<div className="array-historikk">
			<>
				<DollyFieldArray data={[gyldigPersonstatus]} header="Personstatus" nested>
					{(element, idx) => {
						return (
							<PersonstatusVisning
								personstatusData={element}
								idx={idx}
								data={data}
								tmpPersoner={tmpPersoner}
								ident={ident}
							/>
						)
					}}
				</DollyFieldArray>
				{historiskePersonstatuser?.length > 0 && (
					<Panel heading="Personstatus historikk">
						<DollyFieldArray data={historiskePersonstatuser} nested>
							{(element, idx) => {
								return (
									<PersonstatusVisning
										personstatusData={element}
										idx={idx + 1}
										data={data}
										tmpPersoner={tmpPersoner}
										ident={ident}
									/>
								)
							}}
						</DollyFieldArray>
					</Panel>
				)}
			</>
		</div>
	)
}
