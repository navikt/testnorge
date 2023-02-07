import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { PdlDataVisning } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Ident } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

interface PdlMiljoeValues {
	bankIdBruker: boolean
	ident: Ident
	miljoe: string
}

export const PdlPersonMiljoeInfo = ({ bankIdBruker, ident, miljoe }: PdlMiljoeValues) => {
	return (
		<ErrorBoundary>
			<div>
				<SubOverskrift label="PDL" iconKind="visTpsData" />
				<PdlDataVisning ident={ident} bankIdBruker={bankIdBruker} miljoe={miljoe} />
				<p>
					<i>Hold pekeren over PDL/miljø for å se dataene som finnes på denne personen i PDL.</i>
				</p>
			</div>
		</ErrorBoundary>
	)
}
