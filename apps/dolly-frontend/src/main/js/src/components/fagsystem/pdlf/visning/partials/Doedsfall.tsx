import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '@/utils/DataFormatter'
import { DoedsfallData, Person } from '@/components/fagsystem/pdlf/PdlTypes'
import * as _ from 'lodash-es'
import { initialDoedsfall, initialFoedsel } from '@/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

type DoedsfallTypes = {
	data: Array<DoedsfallData>
	tmpPersoner?: Array<DoedsfallData>
	ident?: string
	erPdlVisning?: boolean
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
			<TitleValue title="Dødsdato" value={Formatters.formatDate(doedsfall.doedsdato)} />
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
}: DoedsfallVisningTypes) => {
	const initDoedsfall = Object.assign(_.cloneDeep(initialDoedsfall), data[idx])
	const initialValues = { doedsfall: initDoedsfall }

	const redigertDoedsfallPdlf = _.get(tmpPersoner, `${ident}.person.doedsfall`)?.find(
		(a: Person) => a.id === doedsfall.id
	)
	const slettetDoedsfallPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertDoedsfallPdlf
	if (slettetDoedsfallPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const doedsfallValues = redigertDoedsfallPdlf ? redigertDoedsfallPdlf : doedsfall
	const redigertDoedsfallValues = redigertDoedsfallPdlf
		? { doedsfall: Object.assign(_.cloneDeep(initialFoedsel), redigertDoedsfallPdlf) }
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
		/>
	)
}

export const Doedsfall = ({ data, tmpPersoner, ident, erPdlVisning = false }: DoedsfallTypes) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Dødsfall" iconKind="grav" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(item: DoedsfallData, idx: number) => (
							<DoedsfallVisning
								doedsfall={item}
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
		</div>
	)
}
