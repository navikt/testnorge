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

type FoedselTypes = {
	data: Array<FoedselData>
}

type FoedselVisningTypes = {
	item: FoedselData
	idx: number
}

export const Foedsel = ({ data }: FoedselTypes) => {
	if (!data || data.length === 0) return null

	const FoedselVisning = ({ item, idx }: FoedselVisningTypes) => {
		return (
			<div className="person-visning_content" key={idx}>
				<TitleValue title="Fødselsdato" value={Formatters.formatDate(item.foedselsdato)} />
				<TitleValue title="Fødselsår" value={item.foedselsaar} />
				<TitleValue title="Fødested" value={item.foedested} />
				<TitleValue title="Fødekommune">
					{item.foedekommune && (
						<KodeverkConnector navn="Kommuner" value={item.foedekommune}>
							{(v: Kodeverk, verdi: KodeverkValues) => (
								<span>{verdi ? verdi.label : item.foedekommune}</span>
							)}
						</KodeverkConnector>
					)}
				</TitleValue>
				<TitleValue
					title="Fødeland"
					value={item.foedeland}
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
				/>
			</div>
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
