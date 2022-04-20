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
import { FoedselData } from '~/components/fagsystem/pdlf/PdlTypes'
import { initialFoedsel } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import { VisningRedigerbar } from '~/components/fagsystem/pdlf/visning/VisningRedigerbar'

type FoedselTypes = {
	data: Array<FoedselData>
}

type FoedselVisningTypes = {
	item: FoedselData
	idx: number
}

export const Foedsel = ({
	data,
	put,
	sendOrdrePdl,
	fetch,
	tmpPersoner,
	ident,
	erPdlVisning = false,
}: FoedselTypes) => {
	if (!data || data.length === 0) return null

	const FoedselLes = ({ foedsel, idx }) => {
		return (
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Fødselsdato" value={Formatters.formatDate(foedsel.foedselsdato)} />
				<TitleValue title="Fødselsår" value={foedsel.foedselsaar} />
				<TitleValue title="Fødested" value={foedsel.foedested} />
				<TitleValue title="Fødekommune">
					{foedsel.fodekommune && (
						<KodeverkConnector navn="Kommuner" value={foedsel.fodekommune}>
							{(v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : foedsel.fodekommune}</span>
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

	const FoedselVisning = ({ item, idx }: FoedselVisningTypes) => {
		const initFoedsel = Object.assign(initialFoedsel, data[idx])
		const initialValues = { foedsel: initFoedsel }

		const redigertFoedselPdlf = _get(tmpPersoner, `${ident}.person.foedsel`)?.find(
			(a) => a.id === item.id
		)

		return (
			<>
				<VisningRedigerbar
					dataVisning={
						<FoedselLes foedsel={redigertFoedselPdlf ? redigertFoedselPdlf : item} idx={idx} />
					}
					initialValues={initialValues}
					put={put}
					fetch={fetch}
					sendOrdrePdl={sendOrdrePdl}
					erPdlVisning={erPdlVisning}
					redigertAttributt={redigertFoedselPdlf ? { foedsel: redigertFoedselPdlf } : null}
					path="foedsel"
				/>
			</>
		)
	}

	return (
		<div>
			<SubOverskrift label="Fødsel" iconKind="foedsel" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(item: FoedselData, idx: number) => <FoedselVisning item={item} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
