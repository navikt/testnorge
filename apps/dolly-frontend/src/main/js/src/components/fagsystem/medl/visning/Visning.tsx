import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import MedlVisning from './MedlVisning'

export type Medlemskapsperioder = {
	kilde: string
}

type MedlTypes = {
	data?: any
}

const Medl = ({ data }: MedlTypes) => {
	if (!data) return null

	return <MedlVisning medlemskapsperioder={data} />
}

export default ({ data }: any) => {
	if (!data) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Medlemskapsperioder" iconKind="calendar" />
			<Medl />
		</>
	)
}
