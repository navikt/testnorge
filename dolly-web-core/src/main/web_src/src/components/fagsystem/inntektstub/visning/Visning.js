import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { InntektVisning } from './partials/InntektVisning'
import { FradragVisning } from './partials/FradragVisning'
import { ForskuddstrekkVisning } from './partials/ForskuddstrekkVisning'
import { ArbeidsforholdVisning } from './partials/ArbeidsforholdVisning'

export const InntektstubVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster Inntektstub-data" />
	if (!data) return false

	return (
		<div>
			<SubOverskrift label="A-ordningen (Inntektskomponenten)" iconKind="inntektstub" />
			<DollyFieldArray header="Inntektsinformasjon" data={data} expandable>
				{(id, idx) => (
					<React.Fragment>
						<div className="person-visning_content">
							<TitleValue title="År/måned" value={id.aarMaaned} />
							<TitleValue title="Opplysningspliktig (orgnr/id)" value={id.opplysningspliktig} />
							<TitleValue title="Virksomhet (orgnr/id)" value={id.virksomhet} />
						</div>
						<InntektVisning data={id.inntektsliste} />
						<FradragVisning data={id.fradragsliste} />
						<ForskuddstrekkVisning data={id.forskuddstrekksliste} />
						<ArbeidsforholdVisning data={id.arbeidsforholdsliste} />
					</React.Fragment>
				)}
			</DollyFieldArray>
		</div>
	)
}
