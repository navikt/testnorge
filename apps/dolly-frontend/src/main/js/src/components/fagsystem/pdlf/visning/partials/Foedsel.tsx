import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FoedselData, Person } from '~/components/fagsystem/pdlf/PdlTypes'
import { initialFoedsel } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import _cloneDeep from 'lodash/cloneDeep'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'

type FoedselTypes = {
	data: Array<FoedselData>
	tmpPersoner?: Array<FoedselData>
	ident?: string
	erPdlVisning?: boolean
}

type FoedselLesTypes = {
	foedsel: FoedselData
	idx: number
}

type FoedselVisningTypes = {
	foedsel: FoedselData
	idx: number
	data: Array<FoedselData>
	tmpPersoner: Array<FoedselData>
	ident: string
	erPdlVisning: boolean
}

const FoedselLes = ({ foedsel, idx }: FoedselLesTypes) => {
	return (
		<div className="person-visning_redigerbar" key={idx}>
			<TitleValue title="Fødselsdato" value={Formatters.formatDate(foedsel.foedselsdato)} />
			<TitleValue title="Fødselsår" value={foedsel.foedselsaar} />
			<TitleValue title="Fødested" value={foedsel.foedested} />
			<TitleValue title="Fødekommune">
				{foedsel.foedekommune && (
					<KodeverkConnector navn="Kommuner" value={foedsel.foedekommune}>
						{(_v: Kodeverk, verdi: KodeverkValues) => (
							<span>{verdi ? verdi.label : foedsel.foedekommune}</span>
						)}
					</KodeverkConnector>
				)}
			</TitleValue>
			<TitleValue
				title="Fødeland"
				value={foedsel.foedeland}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
			/>
		</div>
	)
}

const FoedselVisning = ({
	foedsel,
	idx,
	tmpPersoner,
	data,
	erPdlVisning,
	ident,
}: FoedselVisningTypes) => {
	const initFoedsel = Object.assign(_.cloneDeep(initialFoedsel), data[idx])
	const initialValues = { foedsel: initFoedsel }

	const redigertFoedselPdlf = _.get(tmpPersoner, `${ident}.person.foedsel`)?.find(
		(a: Person) => a.id === foedsel.id
	)
	const slettetFoedselPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertFoedselPdlf
	if (slettetFoedselPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const foedselValues = redigertFoedselPdlf ? redigertFoedselPdlf : foedsel
	const redigertFoedselValues = redigertFoedselPdlf
		? { foedsel: Object.assign(_.cloneDeep(initialFoedsel), redigertFoedselPdlf) }
		: null

	return erPdlVisning ? (
		<FoedselLes foedsel={foedsel} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={<FoedselLes foedsel={foedselValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertFoedselValues}
			path="foedsel"
			ident={ident}
		/>
	)
}

export const Foedsel = ({ data, tmpPersoner, ident, erPdlVisning = false }: FoedselTypes) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Fødsel" iconKind="foedsel" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(item: FoedselData, idx: number) => (
							<FoedselVisning
								foedsel={item}
								idx={idx}
								data={data}
								ident={ident}
								erPdlVisning={erPdlVisning}
								tmpPersoner={tmpPersoner}
							/>
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
