import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { formatDate } from '@/utils/DataFormatter'
import _ from 'lodash'
import { DoedsfallData, Person } from '@/components/fagsystem/pdlf/PdlTypes'
import { getInitialFoedsel, initialDoedsfall } from '@/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type DoedsfallTypes = {
	data: Array<DoedsfallData>
	tmpPersoner?: Array<DoedsfallData>
	ident?: string
	erPdlVisning?: boolean
	erRedigerbar?: boolean
}

type DoedsfallVisningTypes = {
	doedsfall: DoedsfallData
	idx: number
	data: Array<DoedsfallData>
	tmpPersoner: Array<DoedsfallData>
	ident: string
	erPdlVisning: boolean
}

type DoedsfallLesTypes = {
	doedsfall: DoedsfallData
	idx: number
}

const DoedsfallLes = ({ doedsfall, idx }: DoedsfallLesTypes) => {
	return (
		<div className="person-visning_content" key={idx}>
			<TitleValue title="Dødsdato" value={formatDate(doedsfall.doedsdato)} />
		</div>
	)
}

const DoedsfallVisning = ({
	doedsfall,
	idx,
	data,
	tmpPersoner,
	ident,
	erPdlVisning,
	master,
}: DoedsfallVisningTypes) => {
	const initDoedsfall = Object.assign(_.cloneDeep(initialDoedsfall), data[idx])
	const initialValues = { doedsfall: initDoedsfall }

	const redigertDoedsfallPdlf = _.get(tmpPersoner, `${ident}.person.doedsfall`)?.find(
		(a: Person) => a.id === doedsfall.id,
	)
	const slettetDoedsfallPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertDoedsfallPdlf
	if (slettetDoedsfallPdlf) {
		return <OpplysningSlettet />
	}

	const doedsfallValues = redigertDoedsfallPdlf ? redigertDoedsfallPdlf : doedsfall
	const redigertDoedsfallValues = redigertDoedsfallPdlf
		? { doedsfall: Object.assign(_.cloneDeep(getInitialFoedsel()), redigertDoedsfallPdlf) }
		: null

	return erPdlVisning ? (
		<DoedsfallLes doedsfall={doedsfall} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={<DoedsfallLes doedsfall={doedsfallValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertDoedsfallValues}
			path="doedsfall"
			ident={ident}
			master={master}
		/>
	)
}

export const Doedsfall = ({
	data,
	pdlfData,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	erRedigerbar = true,
}: DoedsfallTypes) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Dødsfall" iconKind="grav" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(item: DoedsfallData, idx: number) => {
							const master = item?.metadata?.master
							const pdlfElement = pdlfData?.find(
								(element) => element.hendelseId === item?.metadata?.opplysningsId,
							)
							if (erRedigerbar && master !== 'FREG') {
								return (
									<DoedsfallVisning
										doedsfall={master === 'PDL' && pdlfElement ? pdlfElement : item}
										idx={idx}
										data={master === 'PDL' && pdlfData ? pdlfData : data}
										tmpPersoner={tmpPersoner}
										ident={ident}
										erPdlVisning={erPdlVisning}
										master={master}
									/>
								)
							}
							return <DoedsfallLes doedsfall={item} idx={idx} />
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
