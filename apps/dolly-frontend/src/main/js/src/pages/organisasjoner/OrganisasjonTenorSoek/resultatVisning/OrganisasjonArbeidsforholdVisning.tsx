import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { formatStringDates, formatTenorDate } from '@/utils/DataFormatter'

export const OrganisasjonArbeidsforholdVisning = ({ data }: any) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<SubOverskriftExpandable
			label={`Arbeidsforhold (${data.length})`}
			iconKind="arbeid"
			isExpanded={false}
		>
			<div>
				<DollyFieldArray data={data} header={null} nested>
					{(arbeidsforhold: any) => {
						return (
							<TabsVisning kildedata={arbeidsforhold.tenorMetadata?.kildedata}>
								<TitleValue title="Arbeidstaker" value={arbeidsforhold.arbeidstaker} />
								<TitleValue title="Arbeidssted" value={arbeidsforhold.arbeidssted} />
								<TitleValue title="Opplysningspliktig" value={arbeidsforhold.opplysningspliktig} />
								<TitleValue
									title="Startdato"
									value={
										formatTenorDate(arbeidsforhold.startDato) ||
										formatStringDates(arbeidsforhold.startDato)
									}
								/>
								<TitleValue
									title="Sluttdato"
									value={
										formatTenorDate(arbeidsforhold.sluttDato) ||
										formatStringDates(arbeidsforhold.sluttDato)
									}
								/>
								<TitleValue
									title="Arbeidsforholdtype"
									value={arbeidsforhold.arbeidsforholdtypeBeskrivelse}
								/>
								<TitleValue title="Stillingsprosent" value={arbeidsforhold.stillingsprosent} />
								<TitleValue title="Timer per uke" value={arbeidsforhold.timerPerUke} />
								<TitleValue
									title="Ansettelsesform"
									value={arbeidsforhold.ansettelsesformBeskrivelse}
								/>
								<TitleValue title="Permisjoner" value={arbeidsforhold.permisjoner} />
								<TitleValue title="Permitteringer" value={arbeidsforhold.permitteringer} />
								<TitleValue title="Timer med timeloenn" value={arbeidsforhold.timerMedTimeloenn} />
								<TitleValue title="Utenlandsopphold" value={arbeidsforhold.utenlandsopphold} />
								<TitleValue title="Historikk" value={arbeidsforhold.historikk} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
