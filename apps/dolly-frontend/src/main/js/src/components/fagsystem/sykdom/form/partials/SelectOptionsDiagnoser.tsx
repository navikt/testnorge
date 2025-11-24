import { Diagnoser, texts } from './Diagnoser'

export const SelectOptionsDiagnoser = () => {
	return Diagnoser.ICPC2.map((diagnose) => ({
		value: diagnose.code,
		label: `${diagnose.code} - ${diagnose.text}`,
		diagnoseNavn: diagnose.text,
	}))
}

export const SelectOptionsGyldigeDiagnoser = () => {
	return Diagnoser.ICPC2.filter(
		(diagnose) => diagnose.code.charAt(0) !== 'Z' && !Object.values(texts).includes(diagnose.text),
	)?.map((diagnose) => ({
		value: diagnose.code,
		label: `${diagnose.code} - ${diagnose.text}`,
		diagnoseNavn: diagnose.text,
	}))
}
