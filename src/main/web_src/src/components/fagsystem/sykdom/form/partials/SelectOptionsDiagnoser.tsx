import { Diagnoser } from './Diagnoser'

export const SelectOptionsDiagnoser = () => {
	const ICPC2 = Diagnoser.ICPC2
	return ICPC2.map(diagnose => ({
		value: diagnose.code,
		label: `${diagnose.code} - ${diagnose.text}`,
		diagnoseNavn: diagnose.text
	}))
}
