import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { HentPerson } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { PdlForeldreBarn } from '@/components/fagsystem/pdl/visning/partials/relasjoner/PdlForeldreBarn'
import { PdlPartner } from '@/components/fagsystem/pdl/visning/partials/relasjoner/PdlPartner'
import { PdlForeldreansvar } from '@/components/fagsystem/pdl/visning/partials/relasjoner/PdlForeldreansvar'
import { PdlDoedfoedtBarn } from '@/components/fagsystem/pdl/visning/partials/relasjoner/PdlDoedfoedtBarn'

type PdlRelasjonerProps = {
	data: HentPerson
	visTittel?: boolean
}

export const PdlRelasjoner = ({
	data,
	pdlfData,
	tmpPersoner,
	ident,
	identtype,
}: PdlRelasjonerProps) => {
	if (!data) {
		return null
	}

	const partnere = data.sivilstand
	const doedfoedtBarn = data.doedfoedtBarn
	const foreldreBarn = data.forelderBarnRelasjon
	const foreldreansvar = data.foreldreansvar

	if (hasNoValues(partnere) && hasNoValues(doedfoedtBarn) && hasNoValues(foreldreBarn)) {
		return null
	}

	return (
		<ErrorBoundary>
			<>
				<PdlPartner
					data={partnere}
					pdlfData={pdlfData?.sivilstand}
					tmpPersoner={tmpPersoner}
					ident={ident}
					identtype={identtype}
				/>
				<PdlDoedfoedtBarn data={doedfoedtBarn} />
				<PdlForeldreBarn data={foreldreBarn} />
				<PdlForeldreansvar data={foreldreansvar} />
			</>
		</ErrorBoundary>
	)
}

export const hasNoValues = (liste: any[]) => {
	return !liste || liste.length === 0
}
