import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { DoedfoedtBarnData, PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import * as _ from 'lodash-es'
import { initialDoedfoedtBarn } from '@/components/fagsystem/pdlf/form/initialValues'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import React from 'react'

type DataListe = {
	data: Array<DoedfoedtBarnData>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erRedigerbar?: boolean
}

type Data = {
	doedfoedtBarnData?: DoedfoedtBarnData
	idx: number
	data: Array<DoedfoedtBarnData>
	tmpPersoner?: Array<PersonData>
	ident?: string
}

const DoedfoedtBarnLes = ({ data, idx }: Data) => {
	if (!data) {
		return null
	}
	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content" key={idx}>
					<TitleValue title="Dødsdato" value={formatDate(data.dato)} />
				</div>
			</ErrorBoundary>
		</>
	)
}

export const DoedfoedtBarnVisning = ({
	doedfoedtBarnData,
	idx,
	data,
	tmpPersoner,
	ident,
}: Data) => {
	const initDoedfoedtBarn = Object.assign(_.cloneDeep(initialDoedfoedtBarn), data[idx])
	let initialValues = { doedfoedtBarn: initDoedfoedtBarn }

	const redigertDoedfoedtBarnPdlf = _.get(tmpPersoner, `${ident}.person.doedfoedtBarn`)?.find(
		(a: DoedfoedtBarnData) => a.id === doedfoedtBarnData.id,
	)

	const slettetDoedfoedtBarnPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertDoedfoedtBarnPdlf
	if (slettetDoedfoedtBarnPdlf) {
		return <OpplysningSlettet />
	}

	const doedfoedtBarnValues = redigertDoedfoedtBarnPdlf
		? redigertDoedfoedtBarnPdlf
		: doedfoedtBarnData

	let redigertDoedfoedtBarnValues = redigertDoedfoedtBarnPdlf && {
		doedfoedtBarn: Object.assign(_.cloneDeep(initialDoedfoedtBarn), redigertDoedfoedtBarnPdlf),
	}

	return (
		<VisningRedigerbarConnector
			dataVisning={<DoedfoedtBarnLes data={doedfoedtBarnValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertDoedfoedtBarnValues}
			path="doedfoedtBarn"
			ident={ident}
		/>
	)
}

export const DoedfoedtBarn = ({ data, tmpPersoner, ident, erRedigerbar = true }: DataListe) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Dødfødt barn" iconKind="doedfoedt" />
			<DollyFieldArray data={data} nested>
				{(doedfoedtBarn: DoedfoedtBarnData, idx: number) =>
					erRedigerbar ? (
						<DoedfoedtBarnVisning
							doedfoedtBarnData={doedfoedtBarn}
							idx={idx}
							data={data}
							tmpPersoner={tmpPersoner}
							ident={ident}
						/>
					) : (
						<DoedfoedtBarnLes data={doedfoedtBarn} idx={idx} />
					)
				}
			</DollyFieldArray>
		</div>
	)
}
