console.clear();
gsap.config({trialWarn: false});
const tl = gsap.timeline({
  repeat: -1,
  repeatDelay: 1,
  defaults: { duration: 1, ease: "power2.inOut" }
});
//길이 및 시작 각도
gsap.set("#target1", { rotation: 45, svgOrigin: "50 50" });
//second-line-startPlace
gsap.set("#target2", { rotation: 135, svgOrigin: "50 50" });
tl.to("line", { attr: { x2: 100 } });
//회전각
tl.to("#target1", { rotation: 0 }, "turn");
tl.to("#target2", { rotation: 360 }, "turn");
//margin-line
tl.to("#target1", { y: -10 }, "move");
tl.to("#target2", { y: 8 }, "move");
//글자 좌표
tl.to("#theSquare", { attr: { height: 30, y: 40 } }, "move");
//retrun-line
tl.to("line", { attr: { x1: 50, x2: 50 } });
//keyword view
tl.to("text", { duration: 2, opacity: 0, ease: "none" });