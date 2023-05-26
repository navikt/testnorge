import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import MedlVisning from './MedlVisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

export type Medlemskapsperioder = {
	kilde: string
}

type MedlTypes = {
	data?: any
}

const Medl = ({ data }: MedlTypes) => {
	if (!data) {
		return null
	}

	return (
		<DollyFieldArray data={data} header="Medlemskapsperiode">
			{(medlemskap, idx) => (
				<div className="person-visning_content" key={idx}>
					<MedlVisning medlemskapsperiode={medlemskap} />
				</div>
			)}
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
