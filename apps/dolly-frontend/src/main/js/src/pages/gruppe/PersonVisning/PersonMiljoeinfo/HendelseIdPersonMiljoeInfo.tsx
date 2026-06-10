import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { HendelseIdDataVisning } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/HendelseIdDataVisning'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

type RelatertPerson = {
	type: string
	id: string
}

interface HendelseIdPersonMiljoeInfoProps {
	ident: string
	relatertePersoner?: RelatertPerson[]
}

export const HendelseIdPersonMiljoeInfo = ({
	ident,
	relatertePersoner,
}: HendelseIdPersonMiljoeInfoProps) => {
	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="Hendelse-ID" iconKind="vis-tps-data" />
				<HendelseIdDataVisning ident={ident} relatertePersoner={relatertePersoner} />
				<p>
					<i>
						Hold pekeren over for å se hendelse-IDer (Kafka-referanser) for denne personen
						{relatertePersoner?.length > 0 ? ' og relaterte personer' : ''}.
					</i>
				</p>
			</div>
		</ErrorBoundary>
	)
}
