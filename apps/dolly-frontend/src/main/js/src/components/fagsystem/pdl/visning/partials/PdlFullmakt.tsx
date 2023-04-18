import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showKodeverkLabel } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FullmaktData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { FullmaktKodeverk } from '@/config/kodeverk'
import styled from 'styled-components'

type Data = {
	data: FullmaktData
}

type DataListe = {
	data: Array<FullmaktData>
}

const Omraader = styled(TitleValue)`
	&& {
		margin-bottom: 20px;
	}
`

export const Visning = ({ data }: Data) => {
	const omraader = data.omraader
		?.map((omraade) => showKodeverkLabel(FullmaktKodeverk.Tema, omraade))
		?.join(', ')

	return (
		<>
			<div className="person-visning_content">
				<ErrorBoundary>
					<Omraader title="Områder" value={omraader} size={'full-width'} />
					<TitleValue title="Gyldig fra og med" value={formatDate(data.gyldigFraOgMed)} />
					<TitleValue title="Gyldig til og med" value={formatDate(data.gyldigTilOgMed)} />
					<TitleValue title="Motparts personident" value={data.motpartsPersonident} visKopier />
					<TitleValue title="Motparts rolle" value={data.motpartsRolle} />
				</ErrorBoundary>
			</div>
		</>
	)
}

export const PdlFullmakt = ({ data }: DataListe) => {
	if (!data || data.length === 0) {
		return null
	}
	return (
		<div>
			<SubOverskrift label="Fullmakt" iconKind="fullmakt" />
			{/* @ts-ignore */}
			<DollyFieldArray data={data} nested>
				{(fullmakt: FullmaktData) => <Visning data={fullmakt} />}
			</DollyFieldArray>
		</div>
	)
}
