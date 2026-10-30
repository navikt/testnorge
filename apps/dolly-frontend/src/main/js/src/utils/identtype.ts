export type Identtype = 'FNR' | 'DNR' | 'NPID'

export const getIdenttypeFraIdent = (ident?: string | null): Identtype | null => {
	if (!ident || ident.length < 11 || /[^\d]/.test(ident)) {
		return null
	}

	if (parseInt(ident[2]) % 4 >= 2) {
		return 'NPID'
	}
	if (parseInt(ident[0]) >= 4) {
		return 'DNR'
	}
	return 'FNR'
}

export const getMasterForIdenttype = (identtype?: string | null): 'PDL' | 'FREG' =>
	identtype === 'NPID' ? 'PDL' : 'FREG'
