import Panel from '@/components/ui/panel/Panel'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { DoedfoedtBarn } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/doedfoedtBarn/DoedfoedtBarn'
import { ForelderBarnRelasjon } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/ForelderBarnRelasjon'
import { Sivilstand } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/sivilstand/Sivilstand'
import { Foreldreansvar } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/foreldreansvar/Foreldreansvar'
import { UseFormReturn } from 'react-hook-form/dist/types'

export const relasjonerAttributter = [
	'pdldata.person.sivilstand',
	'pdldata.person.forelderBarnRelasjon',
	'pdldata.person.foreldreansvar',
	'pdldata.person.doedfoedtBarn',
]

export const Familierelasjoner = ({ formMethods }: { formMethods: UseFormReturn }) => {
	return (
		<Vis attributt={relasjonerAttributter}>
			<Panel
				heading="Familierelasjoner"
				hasErrors={panelError(relasjonerAttributter)}
				iconType={'relasjoner'}
				startOpen={erForsteEllerTest(formMethods.getValues(), relasjonerAttributter)}
				checkAttributeArray={undefined}
				uncheckAttributeArray={undefined}
			>
				<Kategori title="Sivilstand (partner)" vis="pdldata.person.sivilstand">
					<Sivilstand formMethods={formMethods} />
				</Kategori>
				<Kategori title="Barn/foreldr" vis="pdldata.person.forelderBarnRelasjon">
					<ForelderBarnRelasjon formMethods={formMethods} />
				</Kategori>
				<Kategori title="Foreldreansvar" vis="pdldata.person.foreldreansvar">
					<Foreldreansvar formMethods={formMethods} />
				</Kategori>
				<Kategori title="Dødfødt barn" vis="pdldata.person.doedfoedtBarn">
					<DoedfoedtBarn formMethods={formMethods} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
