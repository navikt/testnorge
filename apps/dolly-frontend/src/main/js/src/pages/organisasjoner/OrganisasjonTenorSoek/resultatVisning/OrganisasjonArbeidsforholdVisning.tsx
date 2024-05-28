import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TabsVisning } from '@/pages/tenorSoek/resultatVisning/TabsVisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import SubOverskriftExpandable from '@/components/ui/subOverskrift/SubOverskriftExpandable'
import { formatTenorDate, oversettBoolean } from '@/utils/DataFormatter'

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
								<TitleValue title="Arbeidssted" value={arbeidsforhold.arbeidssted} />
								<TitleValue title="Opplysningspliktig" value={arbeidsforhold.opplysningspliktig} />
								<TitleValue
									title="Ansettelsesform beskrivelse"
									value={arbeidsforhold.ansettelsesformBeskrivelse}
								/>
								<TitleValue title="Permisjoner" value={arbeidsforhold.permisjoner} />
								<TitleValue
									title="Arbeidsforholdtype beskrivelse"
									value={arbeidsforhold.arbeidsforholdtypeBeskrivelse}
								/>
								<TitleValue title="Timer med timeloenn" value={arbeidsforhold.timerMedTimeloenn} />
								<TitleValue title="Permitteringer" value={arbeidsforhold.permitteringer} />
								<TitleValue
									title="Har permisjoner"
									value={oversettBoolean(arbeidsforhold.harPermisjoner)}
								/>
								<TitleValue
									title="Har permitteringer"
									value={oversettBoolean(arbeidsforhold.harPermitteringer)}
								/>
								<TitleValue
									title="Har historikk"
									value={oversettBoolean(arbeidsforhold.harHistorikk)}
								/>
								<TitleValue title="Arbeidstaker" value={arbeidsforhold.arbeidstaker} />
								<TitleValue title="Timer per uke" value={arbeidsforhold.timerPerUke} />
								<TitleValue
									title="Har utenlandsopphold"
									value={oversettBoolean(arbeidsforhold.harUtenlandsopphold)}
								/>
								<TitleValue title="Stillingsprosent" value={arbeidsforhold.stillingsprosent} />
								<TitleValue title="Utenlandsopphold" value={arbeidsforhold.utenlandsopphold} />
								<TitleValue title="Start dato" value={formatTenorDate(arbeidsforhold.startDato)} />
								<TitleValue title="Historikk" value={arbeidsforhold.historikk} />
								<TitleValue title="Slutt dato" value={arbeidsforhold.sluttDato} />
							</TabsVisning>
						)
					}}
				</DollyFieldArray>
			</div>
		</SubOverskriftExpandable>
	)
}
