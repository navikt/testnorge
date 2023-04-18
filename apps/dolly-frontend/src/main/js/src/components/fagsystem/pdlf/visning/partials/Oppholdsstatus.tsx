import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, uppercaseAndUnderscoreToCapitalized } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { OppholdData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type Data = {
	data: OppholdData
}

type DataListe = {
	data: Array<OppholdData>
}

export const Visning = ({ data }: Data) => {
	return (
		<>
			<div className="person-visning_content">
				<ErrorBoundary>
					<TitleValue title="Oppholdstillatelse fra dato" value={formatDate(data?.oppholdFra)} />
					<TitleValue title="Oppholdstillatelse til dato" value={formatDate(data?.oppholdTil)} />
					<TitleValue
						title="Type oppholdstillatelse"
						value={uppercaseAndUnderscoreToCapitalized(data?.type)}
						size={'medium'}
					/>
				</ErrorBoundary>
			</div>
		</>
	)
}

export const PdlOppholdsstatus = ({ data }: DataListe) => {
	if (!data || data.length === 0) {
		return null
	}
	return (
		<div>
			<SubOverskrift label="Oppholdsstatus" iconKind="udi" />
			<DollyFieldArray data={data} nested>
				{(opphold: OppholdData) => <Visning data={opphold} />}
			</DollyFieldArray>
		</div>
	)
}
