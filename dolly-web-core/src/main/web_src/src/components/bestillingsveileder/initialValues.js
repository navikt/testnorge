import _set from 'lodash/set'
import { mergeKeepShape } from '~/utils/Merge'

export const createInitialValues = (steps, attributter, savedValues, base) => {
	// Initial values from all steps
	let initial = steps.reduce(
		(acc, curr) => Object.assign({}, acc, curr.initialValues(attributter)),
		{}
	)

	// Merge with savedValues
	initial = mergeKeepShape(initial, savedValues)

	// Base values (nyIdent / eksisterende)
	if (base.opprettFraIdenter) {
		// Eksisterende
		initial = _set(initial, 'opprettFraIdenter', base.opprettFraIdenter)
	} else {
		// Ny ident eller Default (dersom man går rett til siden via URL)
		initial = _set(initial, 'antall', base.antall)
		initial = _set(initial, 'tpsf.identtype', base.identtype)
	}

	return initial
}
