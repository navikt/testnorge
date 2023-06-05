import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import MedlVisning from './MedlVisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

export type Medlemskapsperioder = {
	unntakId: number
	ident: string
	fraOgMed: Date
	tilOgMed: Date
	status: string
	statusaarsak: string
	dekning: string
	helsedel: boolean
	medlem: boolean
	lovvalgsland: string
	lovvalg: string
	grunnlag: string
	sporingsinformasjon?: Sporingsinformasjon
	studieinformasjon?: Studieinformasjon
}

export type Sporingsinformasjon = {
	versjon: number
	registrert: Date
	besluttet: Date
	kilde: string
	kildedokument: string
	opprettet: Date
	opprettetAv: string
	sistEndret: Date
	sistEndretAv: string
}

export type Studieinformasjon = {
	statsborgerland: string
	studieland: string
	delstudie: boolean
	soeknadInnvilget: boolean
}

type MedlTypes = {
	data?: Medlemskapsperioder[]
}

const Medl = ({ data }: MedlTypes) => {
	if (!data) {
		return null
	}

	return (
		<DollyFieldArray data={data} header="Medlemskapsperiode" nested>
			{(medlemskap, idx) => {
				return (
					<div className="person-visning_content" key={idx}>
						<MedlVisning medlemskapsperiode={medlemskap} />
					</div>
				)
			}}
		</DollyFieldArray>
	)
}

export default ({ data }: any) => {
	if (!data) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Medlemskapsperioder" iconKind="calendar" />
			<Medl data={data} />
		</>
	)
}
