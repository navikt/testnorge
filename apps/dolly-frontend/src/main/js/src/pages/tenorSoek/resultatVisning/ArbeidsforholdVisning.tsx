import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Arbeidsforhold } from '@/pages/tenorSoek/soekFormPartials/Arbeidsforhold'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'

export const ArbeidsforholdVisning = ({ data }: any) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable label={`Arbeidsforhold (${data.length})`} iconKind="arbeid">
			<div>
				<DollyFieldArray data={data} nested>
					{(arbeidsforhold: any) => {
						console.log('arbeidsforhold: ', arbeidsforhold) //TODO - SLETT MEG
						return (
							<TabsVisning kildedata={arbeidsforhold.tenorMetadata?.kildedata}>
								{/*<Arbeidsforhold arbeidsforhold={arbeidsforhold} />*/}
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
