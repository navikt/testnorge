import { FormikProps } from 'formik'
import { InntektForm } from './inntektForm'
import { FradragForm } from './fradragForm'
import { ForskuddstrekkForm } from './forskuddstrekkForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'

interface InntektsinformasjonLister {
	path: string
	formikBag: FormikProps<{}>
}

export default ({ path, formikBag }: InntektsinformasjonLister) => {
	return (
		<>
			<InntektForm formMethods={formMethods} inntektsinformasjonPath={path} />
			<FradragForm inntektsinformasjonPath={path} />
			<ForskuddstrekkForm inntektsinformasjonPath={path} />
			<ArbeidsforholdForm formMethods={formMethods} inntektsinformasjonPath={path} />
		</>
	)
}
