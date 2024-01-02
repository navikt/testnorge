import * as Yup from 'yup'
import { SigrunstubForm } from '@/components/fagsystem/sigrunstub/form/Form'
import { InntektstubForm } from '@/components/fagsystem/inntektstub/form/Form'
import { InntektsmeldingForm } from '@/components/fagsystem/inntektsmelding/form/Form'
import { AaregForm } from '@/components/fagsystem/aareg/form/Form'
import { BrregstubForm } from '@/components/fagsystem/brregstub/form/Form'
import { PdlfForm } from '@/components/fagsystem/pdlf/form/Form'
import { ArenaForm } from '@/components/fagsystem/arena/form/Form'
import { InstForm } from '@/components/fagsystem/inst/form/Form'
import { UdistubForm } from '@/components/fagsystem/udistub/form/Form'
import { PensjonForm } from '@/components/fagsystem/pensjon/form/Form'
import { DokarkivForm } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import { MedlForm } from '@/components/fagsystem/medl/form/MedlForm'
import { SykdomForm } from '@/components/fagsystem/sykdom/form/Form'
import { OrganisasjonForm } from '@/components/fagsystem/organisasjoner/form/Form'
import { TjenestepensjonForm } from '@/components/fagsystem/tjenestepensjon/form/Form'
import { ifPresent } from '@/utils/YupValidations'
import { AlderspensjonForm } from '@/components/fagsystem/alderspensjon/form/Form'
import { SkjermingForm } from '@/components/fagsystem/skjermingsregister/form/SkjermingForm'
import { ArbeidsplassenForm } from '@/components/fagsystem/arbeidsplassen/form/Form'
import { HistarkForm } from '@/components/fagsystem/histark/form/HistarkForm'
import { UforetrygdForm } from '@/components/fagsystem/uforetrygd/form/Form'
import { SigrunstubPensjonsgivendeForm } from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'
import { KrrstubForm } from '@/components/fagsystem/krrstub/form/KrrForm'
import { MiljoVelger } from '@/components/miljoVelger/MiljoVelger'
import { MalForm } from '@/components/bestillingsveileder/stegVelger/steg/steg3/MalForm'
import { VelgGruppe } from '@/components/bestillingsveileder/stegVelger/steg/steg3/VelgGruppe'

export const DollyValidation = Yup.object({
	...PdlfForm.validation,
	...AaregForm.validation,
	...ArbeidsplassenForm.validation,
	...SigrunstubForm.validation,
	...SigrunstubPensjonsgivendeForm.validation,
	...InntektstubForm.validation,
	...InntektsmeldingForm.validation,
	...SykdomForm.validation,
	...BrregstubForm.validation,
	...InstForm.validation,
	...KrrstubForm.validation,
	...MedlForm.validation,
	...ArenaForm.validation,
	...UdistubForm.validation,
	...SkjermingForm.validation,
	...DokarkivForm.validation,
	...HistarkForm.validation,
	...OrganisasjonForm.validation,
	...MiljoVelger.validation,
	...MalForm.validation,
	...VelgGruppe.validation,
	pensjonforvalter: ifPresent(
		'$pensjonforvalter',
		Yup.object({
			...PensjonForm.validation,
			...TjenestepensjonForm.validation,
			...AlderspensjonForm.validation,
			...UforetrygdForm.validation,
		}),
	),
})
