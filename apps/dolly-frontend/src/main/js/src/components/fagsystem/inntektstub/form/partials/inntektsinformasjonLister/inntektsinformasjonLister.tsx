import { InntektForm } from './inntektForm'
import { FradragForm } from './fradragForm'
import { ForskuddstrekkForm } from './forskuddstrekkForm'
import { ArbeidsforholdForm } from './arbeidsforholdForm'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface InntektsinformasjonLister {
	path: string
	formMethods: UseFormReturn
}

export default ({ path, formMethods }: InntektsinformasjonLister) => {
	return (
		<>
			<InntektForm formMethods={formMethods} inntektsinformasjonPath={path} />
			<FradragForm inntektsinformasjonPath={path} />
			<ForskuddstrekkForm inntektsinformasjonPath={path} />
			<ArbeidsforholdForm formMethods={formMethods} inntektsinformasjonPath={path} />
		</>
	)
}
