export const harGyldigTransaksjonsid = (
	bestillingId: number,
	allTransaksjonsid: any[] | undefined,
) => {
	if (!allTransaksjonsid) return false
	return allTransaksjonsid.some(
		(t: any) => t.bestillingId === bestillingId && t.transaksjonId,
	)
}
